package com.consorcio.api.Utils;

import com.consorcio.api.Model.PrizeModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrizeResult
{
    private PrizeModel prize;
    private String message;
}
