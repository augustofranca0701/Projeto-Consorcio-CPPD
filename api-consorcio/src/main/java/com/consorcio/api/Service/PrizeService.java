package com.consorcio.api.service;

import com.consorcio.api.model.PrizeModel;
import com.consorcio.api.model.UserModel;
import com.consorcio.api.repository.PrizeRepository;
import com.consorcio.api.utils.PrizeResult;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PrizeService {

    @Autowired
    private PrizeRepository prizeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Cria prêmios (prize entries) para um grupo com base na quantidade de pessoas.
     */
    @Transactional
    public void createPrizesByGroup(com.consorcio.api.model.GroupModel group) {
        int numPrizes = group.getQuantidadePessoas();
        LocalDate groupCreationDate = group.getDataCriacao();

        for (int i = 1; i <= numPrizes; i++) {
            PrizeModel prize = new PrizeModel();
            prize.setDatePrize(groupCreationDate.plusMonths(i));
            // associa o grupo (JPA cuida da FK)
            prize.setGrupo(group);
            prize.setUser_id(null);
            prizeRepository.save(prize);
        }
    }

    /**
     * Sorteia um usuário sem prêmio para o próximo prêmio disponível do grupo.
     */
    @Transactional
    public ResponseEntity<Map<String, Object>> sortUserForPrize(Long groupId) {
        PrizeModel nextPrize = findFirstPrizeAvailable(groupId);
        if (nextPrize == null) {
            Map<String, Object> noPrizeResponse = new HashMap<>();
            noPrizeResponse.put("message", "No more prizes available.");
            return new ResponseEntity<>(noPrizeResponse, HttpStatus.NOT_FOUND);
        }

        List<UserModel> users = usersWithoutPrizes(groupId);
        if (users.isEmpty()) {
            Map<String, Object> noPrizeResponse = new HashMap<>();
            noPrizeResponse.put("message", "No users without prizes.");
            return new ResponseEntity<>(noPrizeResponse, HttpStatus.NOT_FOUND);
        }

        // seleciona aleatoriamente um usuário
        Random random = new Random();
        int randomUserIndex = random.nextInt(users.size());
        UserModel userSorted = users.get(randomUserIndex);

        // atualiza o prêmio com o usuário sorteado
        nextPrize.setUser_id(userSorted.getId());
        prizeRepository.save(nextPrize);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = nextPrize.getDatePrize().format(dtf);

        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("message", "O usuário: " + userSorted.getName() + " foi sorteado para a data de prêmio " + formattedDate);
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    /**
     * Busca o primeiro prêmio disponível (user_id IS NULL) para o grupo, ordenado por date_prize asc.
     */
    public PrizeModel findFirstPrizeAvailable(Long groupId) {
        String sql = "SELECT p.* FROM prizes p WHERE p.group_id = ?1 AND p.user_id IS NULL ORDER BY p.date_prize ASC LIMIT 1";
        Query query = entityManager.createNativeQuery(sql, PrizeModel.class);
        query.setParameter(1, groupId);

        List<PrizeModel> resultList = query.getResultList();

        if (resultList == null || resultList.isEmpty()) {
            return null;
        } else {
            return resultList.get(0);
        }
    }

    /**
     * Retorna todos os usuários que ainda não possuem prêmio no grupo (consulta via tabela de join user_group).
     */
    public List<UserModel> usersWithoutPrizes(Long groupId) {
        String sql = """
                SELECT
                    u.*
                FROM
                    user_group
                INNER JOIN users u ON u.id = user_group.user_id
                LEFT JOIN prizes p ON p.group_id = ?1 AND p.user_id = u.id
                WHERE
                    p.user_id IS NULL
                """;

        Query query = entityManager.createNativeQuery(sql, UserModel.class);
        query.setParameter(1, groupId);

        @SuppressWarnings("unchecked")
        List<UserModel> users = query.getResultList();
        return users;
    }

    // --- Métodos auxiliares/para testes ---

    /**
     * Endpoint auxiliar para retornar o primeiro prêmio disponível (somente para testes).
     */
    public ResponseEntity<Object> findFirstAvailablePrizeByGroupId(Long groupId) {
        try {
            String sql = "SELECT p.* FROM prizes p WHERE p.group_id = ?1 AND p.user_id IS NULL ORDER BY p.date_prize ASC LIMIT 1";
            Query query = entityManager.createNativeQuery(sql, PrizeModel.class);
            query.setParameter(1, groupId);

            @SuppressWarnings("unchecked")
            List<PrizeModel> resultList = query.getResultList();

            if (resultList == null || resultList.isEmpty()) {
                Map<String, Object> noPrizeResponse = new HashMap<>();
                noPrizeResponse.put("message", "No more prizes available.");
                return new ResponseEntity<>(noPrizeResponse, HttpStatus.NOT_FOUND);
            } else {
                PrizeResult result = new PrizeResult();
                result.setPrize(resultList.get(0));
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", 500);
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retorna os usuários sem prêmios (API auxiliar para testes).
     */
    public ResponseEntity<Object> getUsersWithoutPrizes(Long groupId) {
        try {
            String sql = """
                    SELECT
                        u.*
                    FROM
                        user_group
                    INNER JOIN users u ON u.id = user_group.user_id
                    LEFT JOIN prizes p ON p.group_id = ?1 AND p.user_id = u.id
                    WHERE
                        p.user_id IS NULL
                    """;

            Query query = entityManager.createNativeQuery(sql, UserModel.class);
            query.setParameter(1, groupId);

            @SuppressWarnings("unchecked")
            List<UserModel> unassignedUsers = query.getResultList();

            if (unassignedUsers == null || unassignedUsers.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", 404);
                errorResponse.put("message", "No users without prizes.");
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(unassignedUsers, HttpStatus.OK);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", 500);
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
