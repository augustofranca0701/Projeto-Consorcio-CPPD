package com.consorcio.api.Service;

import com.consorcio.api.DTO.UserDTO.PaymentDTO;
import com.consorcio.api.Model.GroupModel;
import com.consorcio.api.Model.PaymentModel;
import com.consorcio.api.Model.UserModel;
import com.consorcio.api.Repository.PaymentRepository;
import com.consorcio.api.Repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService
{
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void createPaymentsByGroup(UserModel user, GroupModel group)
    {
        int numPayments = group.getQuantidadePessoas();
        LocalDate groupCreationDate = group.getDataCriacao();
        Long valor = group.getValorParcelas();

        for (int i = 1; i <= numPayments; i++) {
            PaymentModel payment = new PaymentModel();
            payment.setDataVencimento(groupCreationDate.plusMonths(i));
            payment.setValor(valor);
            payment.setIsPaid(false);

            // Set the GrupoModel object (JPA handles the foreign key)
            payment.setGroup(group);
            payment.setUser(user);

            paymentRepository.save(payment);
        }
    }

    public ResponseEntity<Object> findAllUserPayments(Long userId)
    {
        try
        {
            String sql = """                        
                        SELECT
                            p.id,
                            p.data_vencimento,
                            p.valor,
                            p.is_paid,
                            g.name nome_grupo
                        FROM
                            payments p
                        LEFT JOIN users u ON u.id = p.user_id
                        LEFT JOIN groups g ON g.id = p.group_id
                        WHERE
                            u.id = ?1
                        ORDER BY
                            p.data_vencimento;
                        """;

            Query query = entityManager.createNativeQuery(sql, PaymentDTO.class);
            query.setParameter(1, userId);

            List<PaymentDTO> userPayments = query.getResultList();

            return new ResponseEntity<>(userPayments, HttpStatus.OK);
        }
        catch (Exception e)
        {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", 500);
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> makePayment(Long userId, Long paymentId)
    {
        try {
            UserModel user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found!"));

            PaymentModel payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new EntityNotFoundException("Payment with id " + paymentId + " not found!"));

            if (!payment.getUser().getId().equals(user.getId())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", 400);
                errorResponse.put("message", "Payment does not belong to User.");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            payment.setIsPaid(true);
            paymentRepository.save(payment);

            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("error", 200);
            successResponse.put("message", "Payment updated successfully!");
            return new ResponseEntity<>(successResponse, HttpStatus.OK);
        }
        catch (EntityNotFoundException e)
        {
            // Handle EntityNotFoundExceptions specifically
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", 404);
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        catch (Exception e)
        {
            // Handle other exceptions generically
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", 500);
            errorResponse.put("message", "An error occurred while updating the payment.");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
