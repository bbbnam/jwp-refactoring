package kitchenpos.tablegroup.application;

import kitchenpos.order.domain.OrderRepository;
import kitchenpos.order.domain.OrderStatus;
import kitchenpos.table.domain.OrderTable;
import kitchenpos.table.domain.OrderTableRepository;
import kitchenpos.table.domain.OrderTables;
import kitchenpos.table.exception.IllegalOrderTableException;
import kitchenpos.table.exception.NotInitOrderTablesException;
import kitchenpos.tablegroup.domain.TableGroup;
import kitchenpos.tablegroup.domain.TableGroupRepository;
import kitchenpos.tablegroup.dto.TableGroupRequest;
import kitchenpos.tablegroup.dto.TableGroupResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Transactional
@Service
public class TableGroupService {
    private final OrderRepository orderRepository;
    private final OrderTableRepository orderTableRepository;
    private final TableGroupRepository tableGroupRepository;

    public TableGroupService(final OrderRepository orderRepository, final OrderTableRepository orderTableRepository,
                             final TableGroupRepository tableGroupRepository) {
        this.orderRepository = orderRepository;
        this.orderTableRepository = orderTableRepository;
        this.tableGroupRepository = tableGroupRepository;
    }

    public TableGroupResponse create(final TableGroupRequest request) {
        OrderTables orderTables = new OrderTables(request.getOrderTables());
        checkInitOrderTables(orderTables);
        final TableGroup savedTableGroup = tableGroupRepository.save(
                new TableGroup(orderTables));

        return TableGroupResponse.from(savedTableGroup);
    }

    private void checkInitOrderTables(OrderTables orderTables) {
        List<OrderTable> savedOrderTables = orderTableRepository.findAllById(orderTables.toOrderTableIds());

        if (orderTables.size() != savedOrderTables.size()) {
            throw new NotInitOrderTablesException();
        }

        for (final OrderTable savedOrderTable : savedOrderTables) {
            checkOrderTableEmptyOrGroupNull(savedOrderTable);
        }
    }

    private void checkOrderTableEmptyOrGroupNull(OrderTable savedOrderTable) {
        if (!savedOrderTable.isEmpty() || Objects.nonNull(savedOrderTable.getTableGroupId())) {
            throw new IllegalOrderTableException();
        }
    }

    public void ungroup(final Long tableGroupId) {
        final OrderTables orderTables = new OrderTables(orderTableRepository.findAllByTableGroupId(tableGroupId));
        final List<Long> orderTableIds = orderTables.toOrderTableIds();
        checkOrderTableIdAndStatus(orderTableIds);

        orderTables.ungroup();
    }

    private void checkOrderTableIdAndStatus(List<Long> orderTableIds) {
        if (orderRepository.existsByOrderTableIdInAndOrderStatusIn(
                orderTableIds, Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name()))) {
            throw new IllegalOrderTableException();
        }
    }
}
