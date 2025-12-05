package com.consorcio.api.utils;

import com.consorcio.api.model.PrizeModel;

import java.util.List;

public class PrizeResult {

    private PrizeModel prize;
    private List<PrizeModel> prizes;

    public PrizeResult() {}

    public PrizeResult(PrizeModel prize) {
        this.prize = prize;
    }

    public PrizeResult(List<PrizeModel> prizes) {
        this.prizes = prizes;
    }

    public PrizeModel getPrize() {
        return prize;
    }

    public void setPrize(PrizeModel prize) {
        this.prize = prize;
    }

    public List<PrizeModel> getPrizes() {
        return prizes;
    }

    public void setPrizes(List<PrizeModel> prizes) {
        this.prizes = prizes;
    }
}
