package toeicLab.toeicLab.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toeicLab.toeicLab.domain.StudyGroup;
import toeicLab.toeicLab.repository.StudyGroupRepository;


@Service
@Transactional
@RequiredArgsConstructor
public class StudyGroupService {
    private final StudyGroupRepository studyGroupRepository;

    /**
     * 스터디 그룹의 조장을 바꾼다.
     * @param studyGroupId
     * @param targetId
     */
    public void changeLeader(Long studyGroupId, Long targetId) {
        StudyGroup studyGroup = studyGroupRepository.getOne(studyGroupId);
        studyGroup.setReaderId(targetId);
        studyGroupRepository.save(studyGroup);
    }

    /**
     * 스터디 그룹의 이름을 수정한다.
     * @param studyGroupId
     * @param name
     */
    public void changeName(Long studyGroupId, String name) {
        StudyGroup studyGroup = studyGroupRepository.getOne(studyGroupId);
        studyGroup.setName(name);
        studyGroupRepository.save(studyGroup);
    }

}
