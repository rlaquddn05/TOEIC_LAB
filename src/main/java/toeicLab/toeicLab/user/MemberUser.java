package toeicLab.toeicLab.user;

import lombok.Getter;
import org.springframework.security.core.userdetails.User;
import toeicLab.toeicLab.domain.Member;

import java.util.List;

@Getter
public class MemberUser extends User {
    private Member member;
    public MemberUser(Member member){
        super(member.getUserId(), member.getPassword(), List.of(member.getMemberType()));
        this.member = member;
    }
}
