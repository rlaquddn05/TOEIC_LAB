package toeicLab.toeicLab.user;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import toeicLab.toeicLab.domain.StudyGroupApplicationTag;

@Component
@RequiredArgsConstructor
public class StudyGroupApplicationValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        StudyGroupApplicationForm studyGroupApplicationForm = (StudyGroupApplicationForm) target;
        double value = 1;
        StudyGroupApplicationTag[] tags = studyGroupApplicationForm.getTags();
        for (int i = 0; i < tags.length; i++) {
            value = value * tags[i].get();
        }

        if (studyGroupApplicationForm.getAgreement() == null) {
            errors.rejectValue(
                    "agreement",
                    "must agree",
                    new Object[]{studyGroupApplicationForm.getAgreement()},
                    "이용약관에 동의하셔야 합니다."
            );
        }

        if (value % 2 != 0 && value % 3 != 0 && value % 5 != 0) {
            errors.rejectValue(
                    "tags",
                    "age tag not selected",
                    new Object[]{studyGroupApplicationForm.getAgreement()},
                    "희망연령을 선택하셔야 합니다."
            );
        }

        if (value % 7 != 0 && value % 11 != 0 && value % 13 != 0) {
            errors.rejectValue(
                    "tags",
                    "level tag not selected",
                    new Object[]{studyGroupApplicationForm.getAgreement()},
                    "희망레벨을 선택하셔야 합니다."
            );
        }

        if (value % 17 != 0 && value % 19 != 0) {
            errors.rejectValue(
                    "tags",
                    "day tag not selected",
                    new Object[]{studyGroupApplicationForm.getAgreement()},
                    "희망요일을 선택하셔야 합니다."
            );
        }

        if (value % 23 != 0 && value % 29 != 0) {
            errors.rejectValue(
                    "tags",
                    "gender tag not selected",
                    new Object[]{studyGroupApplicationForm.getAgreement()},
                    "희망성별을 선택하셔야 합니다."
            );
        }

    }
}
