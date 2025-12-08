package com.consorcio.api.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "uuid", unique = true, updatable = false)
    private UUID uuid = UUID.randomUUID();

    @Column(length = 255)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(unique = true, length = 20)
    private String cpf;

    @Column(unique = true, length = 50)
    private String phone;

    @Column(length = 255)
    private String address;

    @Column(length = 128)
    private String city;

    @Column(length = 64)
    private String state;

    @Column(length = 255)
    private String complement;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    // ADICIONADO: relacionamento Many-to-Many com GroupModel
    @ManyToMany
    @JoinTable(
        name = "user_group",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private List<GroupModel> groups = new ArrayList<>();

    public UserModel() {}

    public UserModel(Long id, UUID uuid, String name, String email, String password, String cpf, String phone,
                     String address, String city, String state, String complement,
                     Instant createdAt, Instant updatedAt, Instant deletedAt) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.email = email;
        this.password = password;
        this.cpf = cpf;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.complement = complement;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UUID getUuid() { return uuid; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @JsonIgnore
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getComplement() { return complement; }
    public void setComplement(String complement) { this.complement = complement; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Instant getDeletedAt() { return deletedAt; }
    public void setDeletedAt(Instant deletedAt) { this.deletedAt = deletedAt; }

    public List<GroupModel> getGroups() { return groups; }
    public void setGroups(List<GroupModel> groups) { this.groups = groups; }
}
