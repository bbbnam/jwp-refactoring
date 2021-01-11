package kitchenpos.domain.order.exceptions;

import kitchenpos.exceptions.EntityNotFoundException;

public class MenuEntityNotFoundException extends EntityNotFoundException {
    public MenuEntityNotFoundException(final String message) {
        super(message);
    }
}