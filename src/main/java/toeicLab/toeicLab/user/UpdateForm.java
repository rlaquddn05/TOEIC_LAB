package toeicLab.toeicLab.user;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class UpdateForm {
    @NotBlank
    private String userId;

    @NotBlank
    private String password;

    @NotBlank
    private String gender;

    @NotBlank
    private Integer age;

    @NotBlank
    private String contact;

    @NotBlank
    private String nickname;

    @NotBlank
    private String zipcode;
    private String city;
    private String street;

}
