package client;

import java.math.BigInteger;

public class Result {

    private BigInteger bigInt;

    public Result(BigInteger bigInt) {
        this.bigInt = bigInt;
    }

    public BigInteger getFactorial() {
        return this.bigInt;
    }
}
