package cn.itsource.aigou.domain;

/**
 * @author solargen
 * @version V1.0
 * @className Employee
 * @description TODO
 * @date 2019/5/11
 */
public class Employee {

    private Long id;
    private String username;
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
