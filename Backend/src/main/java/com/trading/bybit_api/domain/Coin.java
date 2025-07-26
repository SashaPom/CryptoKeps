package com.trading.bybit_api.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Coin {
    BTC("BTC"),
    USDT("USDT"),
    ETH("ETH"),

//    XRP("XRP"),
//    LTC("LTC"),
//    EOS("EOS"),
//    TRX("TRX"),
//    ADA("ADA"),
//    XLM("XLM"),
//    LINK("LINK"),
//    XTZ("XTZ"),
//    BCH("BCH"),
//    XMR("XMR"),
//    DASH("DASH"),
//    ZEC("ZEC"),
//    ETC("ETC"),
//    ATOM("ATOM"),
//    ONT("ONT"),
//    IOTA("IOTA"),
//    VET("VET"),
//    NEO("NEO"),
//    QTUM("QTUM"),
//    ZRX("ZRX"),
//    BAT("BAT"),
//    OMG("OMG"),
//    ICX("ICX"),
//    LSK("LSK"),
//    NANO("NANO"),
//    WAVES("WAVES"),
//    REP("REP"),
//    ENJ("ENJ"),
//    MKR("MKR"),
//    KNC("KNC"),
//    CRO("CRO"),
//    PAX("PAX"),
//    USDC("USDC"),
//    TUSD("TUSD"),
//    BUSD("BUSD"),
//    DAI("DAI"),
//    COMP("COMP"),
//    BAL("BAL"),
//    YFI("YFI"),
//    UNI("UNI"),
//    REN("REN"),
//    UMA("UMA"),
//    CRV("CRV"),
//    SUSHI("SUSHI"),
//    AAVE("AAVE"),
//    SNX("SNX"),
//    BAND("BAND")
    ;

    private final String symbol;

    // Custom lookup method
    public static Coin fromSymbolIgnoreCase(String symbol) {
        for (Coin coin : values()) {
            if (coin.getSymbol().equalsIgnoreCase(symbol)) {
                return coin;
            }
        }
        throw new IllegalArgumentException("No coin with symbol " + symbol);
    }
    }
