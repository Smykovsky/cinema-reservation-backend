package pl.smyk.authservice.model;

public enum Permission {
    // User permissions
    USER_READ,
    USER_WRITE,
    USER_DELETE,

    // Operator permissions
    OPERATOR_READ,
    OPERATOR_WRITE,
    OPERATOR_DELETE,

    // Admin permissions
    ADMIN_READ,
    ADMIN_WRITE,
    ADMIN_DELETE,

    // Inne przykłady
    ORDER_READ,
    ORDER_CREATE,
    ORDER_UPDATE,
    ORDER_DELETE,

    PRODUCT_READ,
    PRODUCT_CREATE,
    PRODUCT_UPDATE,
    PRODUCT_DELETE
}