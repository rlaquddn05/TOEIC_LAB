package toeicLab.toeicLab.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import toeicLab.toeicLab.repository.MemberRepository;

@Component
public class SignUpValidator implements Validator {

    @Autowired
    MemberRepository memberRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignUpForm signUpForm = (SignUpForm)target;

        if(memberRepository.existsByUserId(signUpForm.getUserId())) {
            errors.rejectValue(
                    "userId",
                    "duplicated.userId",
                    new Object[]{signUpForm.getUserId()},
                    "이미 사용중인 아이디입니다."
            );
        }

        if(!((SignUpForm) target).getPassword().equals(((SignUpForm) target).getCheck_password())){
            errors.rejectValue(
                    "password",
                    "notsame.password",
                    new Object[]{signUpForm.getPassword()},
                    "두 비밀번호가 일치하지 않습니다."
            );
        }

        if(memberRepository.existsByEmail(signUpForm.getEmail())) {
            errors.rejectValue(
                    "email",
                    "duplicated.email",
                    new Object[]{signUpForm.getEmail()},
                    "이미 사용중인 이메일입니다."
            );
        }

        if(memberRepository.existsByNickname(signUpForm.getNickname())) {
            errors.rejectValue(
                    "nickname",
                    "duplicated.nickname",
                    new Object[]{signUpForm.getNickname()},
                    "이미 사용중인 닉네임입니다."
            );
        }

        if(memberRepository.existsByContact(signUpForm.getContact())) {
            errors.rejectValue(
                    "contact",
                    "duplicated.contact",
                    new Object[]{signUpForm.getContact()},
                    "이미 사용중인 전화번호입니다."
            );
        }
    }
}
