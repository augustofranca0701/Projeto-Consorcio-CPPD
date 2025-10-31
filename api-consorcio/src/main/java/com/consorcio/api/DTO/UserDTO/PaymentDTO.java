package com.consorcio.api.DTO.UserDTO;

import java.util.Date;

public record PaymentDTO(Long id, Date dataVencimento, Long valor, Boolean isPaid, String nomeGrupo)
{
}
