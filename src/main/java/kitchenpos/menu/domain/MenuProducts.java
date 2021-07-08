package kitchenpos.menu.domain;

import java.util.List;
import java.util.stream.Collectors;

public class MenuProducts {
    private final List<MenuProduct> menuProducts;

    public MenuProducts(List<MenuProduct> menuProducts) {
        this.menuProducts = menuProducts;
    }

    public List<Long> toMenuProductIds() {
        return this.menuProducts.stream()
                .map(MenuProduct::getProductId)
                .collect(Collectors.toList());
    }

}
