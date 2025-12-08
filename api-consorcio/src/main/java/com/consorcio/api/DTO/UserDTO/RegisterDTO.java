package com.consorcio.api.dto.UserDTO;

public class RegisterDTO {
    private String name;
    private String email;
    private String password;
    private String cpf;
    private String phone;
    private String address;
    private String complement;
    private String city;
    private String state;

    public RegisterDTO() {}

    // getters e setters para TODOS os campos
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getComplement() { return complement; }
    public void setComplement(String complement) { this.complement = complement; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}
