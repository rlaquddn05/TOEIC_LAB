package toeicLab.toeicLab.domain;

public enum QuestionSetType {

    QUARTER_TOEIC(1),
    HALF_TOEIC(2),
    FULL_TOEIC(3),
    HALF_SPEAKING(4),
    FULL_SPEAKING(5),
    MEETING(6),
    LEVEL_TEST(7);

    int primary;

    QuestionSetType(int p){
        primary = p;
    }

    public int get() {
        return primary;
    }
}
