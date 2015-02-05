package eip.com.lizz.AbstractClasses;


public abstract class        APaymentMethod {

    /* Enums */
    public enum             Type {
        CREDIT_CARD,
        PAYPAL,
        WALLET
    }

    /* Attributes */
    private Type            _paymentMethodType;

    /* Methods */
    public abstract String  toString();

    /* Getters and setters*/

    public Type get_paymentMethodType() {
        return _paymentMethodType;
    }

    public void set_paymentMethodType(Type _paymentMethodType) {
        this._paymentMethodType = _paymentMethodType;
    }
}
