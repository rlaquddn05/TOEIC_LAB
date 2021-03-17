package toeicLab.toeicLab.domain;

public enum StudygroupApplicaionTag {
    AGE_10S(2), AGE_20S(3), AGE_30S(5), AGE_ALL(30),
    LEVEL_BEGINNER(7), LEVEL_INTERMEDIATE(11), LEVEL_ADVANCED(13), LEVEL_ALL(7 * 11 * 13),
// TODO     LOCAL(), ALL_AREA(),
    WEEKDAY(17), WEEKEND(19),
    MALE(23), FEMALE(29),
    NONE(1), EVERYTHING(1);

    int primary;

    StudygroupApplicaionTag(int p){
        primary = p;
    }

    public int get() {
        return primary;
    }

    
}
