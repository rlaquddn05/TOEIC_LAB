package toeicLab.toeicLab.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toeicLab.toeicLab.domain.Member;
import toeicLab.toeicLab.domain.StudyGroup;
import toeicLab.toeicLab.repository.StudyGroupRepository;


@Service
@Transactional
@RequiredArgsConstructor
public class StudyGroupService {
    private final StudyGroupRepository studyGroupRepository;

    public void changeLeader(Long studyGroupId, Long targetId) {
        StudyGroup studyGroup = studyGroupRepository.getOne(studyGroupId);
        studyGroup.setReaderId(targetId);
        studyGroupRepository.save(studyGroup);
    }

    public void changeName(Long studyGroupId, String name) {
        StudyGroup studyGroup = studyGroupRepository.getOne(studyGroupId);
        studyGroup.setName(name);
        studyGroupRepository.save(studyGroup);
    }

    public void signOutStudyGroup(Member member) {

    }
}
