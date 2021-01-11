package kitchenpos.infra.order;

import kitchenpos.domain.orderTable.OrderTable;
import kitchenpos.domain.orderTable.OrderTableRepository;
import kitchenpos.domain.order.exceptions.InvalidTryOrderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderTableAdapterTest {
    private OrderTableAdapter orderTableAdapter;

    @Mock
    private OrderTableRepository orderTableRepository;

    @BeforeEach
    void setup() {
        orderTableAdapter = new OrderTableAdapter(orderTableRepository);
    }

    @DisplayName("존재하지 않는 테이블에 주문 여부 확인 시 예외 발생")
    @Test
    void canOrderAtThisTableFailByNoExistTest() {
        // given
        Long orderTableId = 1L;
        given(orderTableRepository.findById(orderTableId)).willThrow(new InvalidTryOrderException(""));

        // when, then
        assertThatThrownBy(() -> orderTableAdapter.canOrderAtThisTable(orderTableId))
                .isInstanceOf(InvalidTryOrderException.class);
    }

    @DisplayName("비어있는 주문 테이블에 주문 여부 확인 시 예외 발생")
    @Test
    void canOrderAtThisTableFailWithEmptyTableTest() {
        // given
        Long orderTableId = 1L;
        given(orderTableRepository.findById(orderTableId))
                .willReturn(Optional.of(new OrderTable(0, true)));

        // when, then
        assertThatThrownBy(() -> orderTableAdapter.canOrderAtThisTable(orderTableId))
                .isInstanceOf(InvalidTryOrderException.class)
                .hasMessage("비어있는 주문 테이블에서 주문할 수 없습니다.");
    }
}