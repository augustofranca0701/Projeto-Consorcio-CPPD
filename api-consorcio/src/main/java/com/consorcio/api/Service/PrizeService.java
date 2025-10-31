package com.consorcio.api.Service;

import com.consorcio.api.Model.GroupModel;
import com.consorcio.api.Model.PrizeModel;
import com.consorcio.api.Model.UserModel;
import com.consorcio.api.Repository.PrizeRepository;
import com.consorcio.api.Utils.PrizeResult;
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
public class PrizeService
{
    @Autowired
    private PrizeRepository prizeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void createPrizesByGroup(GroupModel group)
    {
        int numPrizes = group.getQuantidadePessoas();
        LocalDate groupCreationDate = group.getDataCriacao();

        for (int i = 1; i <= numPrizes; i++) {
            PrizeModel prize = new PrizeModel();
            prize.setDatePrize(groupCreationDate.plusMonths(i));

            // Set the GrupoModel object (JPA handles the foreign key)
            prize.setGrupo(group);

            prize.setUser_id(null);

            prizeRepository.save(prize);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> sortUserForPrize(Long groupId)
    {
        PrizeModel nextPrize = findFirstPrizeAvailable(groupId);
        if (nextPrize == null)
        {
            Map<String, Object> noPrizeResponse = new HashMap<>();
            noPrizeResponse.put("message", "No more prizes available.");
            return new ResponseEntity<>(noPrizeResponse, HttpStatus.NOT_FOUND);
        }

        List<UserModel> users = usersWithoutPrizes(groupId);
        if(users.isEmpty())
        {
            Map<String, Object> noPrizeResponse = new HashMap<>();
            noPrizeResponse.put("message", "No users without prizes.");
            return new ResponseEntity<>(noPrizeResponse, HttpStatus.NOT_FOUND);
        }

        // Randomly select a user from the list
        Random random = new Random();
        int randomUserIndex = random.nextInt(users.size());
        UserModel userSorted = users.get(randomUserIndex);

        // Update the nextPrize object with the chosen user's ID
        nextPrize.setUser_id(userSorted.getId());
        prizeRepository.save(nextPrize);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = nextPrize.getDatePrize().format(dtf);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", "The user: " + userSorted.getName() + " has been sorted to prize date " + formattedDate);
        return new ResponseEntity<>(errorResponse, HttpStatus.OK);
    }

    public PrizeModel findFirstPrizeAvailable(Long groupId)
    {
        String sql = "SELECT p.* FROM prizes p WHERE p.group_id = ?1 AND p.user_id IS NULL ORDER BY p.date_prize ASC LIMIT 1";
        Query query = entityManager.createNativeQuery(sql, PrizeModel.class);
        query.setParameter(1, groupId);

        List<PrizeModel> resultList = query.getResultList();

        if (resultList.isEmpty())
        {
            return null;
        } else {
            return resultList.getFirst();
        }

    }

    public List<UserModel> usersWithoutPrizes(Long groupId)
    {
        String sql = """
                            SELECT
                                u.*
                            FROM
                                user_group
                            INNER JOIN users u ON u.id = user_group.user_id
                            LEFT JOIN prizes p ON p.group_id = ?1 AND p.user_id = u.id
                            WHERE
                                p.user_id IS NULL;
                      """;

        Query query = entityManager.createNativeQuery(sql, UserModel.class);
        query.setParameter(1, groupId);

        return query.getResultList();
    }














    // Apenas para testes

    public ResponseEntity<Object> findFirstAvailablePrizeByGroupId(Long groupId)
    {
        try
        {
            String sql = "SELECT p.* FROM prizes p WHERE p.group_id = ?1 AND p.user_id IS NULL ORDER BY p.date_prize ASC LIMIT 1";
            Query query = entityManager.createNativeQuery(sql, PrizeModel.class);
            query.setParameter(1, groupId);

            List<PrizeModel> resultList = query.getResultList();

            if (resultList.isEmpty())
            {
                Map<String, Object> noPrizeResponse = new HashMap<>();
                noPrizeResponse.put("message", "No more prizes available.");
                return new ResponseEntity<>(noPrizeResponse, HttpStatus.NOT_FOUND);
            }
            else
            {
                PrizeResult result = new PrizeResult();
                result.setPrize(resultList.getFirst());
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
        }
        catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", 500);
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> getUsersWithoutPrizes(Long groupId)
    {
        try
        {
            String sql = """
                            SELECT
                                u.*
                            FROM
                                user_group
                            INNER JOIN users u ON u.id = user_group.user_id
                            LEFT JOIN prizes p ON p.group_id = ?1 AND p.user_id = u.id
                            WHERE
                                p.user_id IS NULL;
                        """;

            Query query = entityManager.createNativeQuery(sql, UserModel.class);
            query.setParameter(1, groupId);

            List<UserModel> unassignedUsers = query.getResultList();

            if (unassignedUsers.isEmpty())
            {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", 404);
                errorResponse.put("message", "No users without prizes.");
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            else
            {
                return new ResponseEntity<>(unassignedUsers, HttpStatus.OK);
            }
        }
        catch (Exception e)
        {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", 500);
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
