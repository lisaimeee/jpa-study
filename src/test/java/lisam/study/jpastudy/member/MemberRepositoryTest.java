package lisam.study.jpastudy.member;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManagerFactory;

@ExtendWith(SpringExtension.class)
@Sql(value = {"classpath:createTeam.sql", "classpath:createMember.sql"})
@DisplayName("member와 team의 관계가 manyToOne이고, member에서 team을 레이지로딩 설정했을 때 테스트")
@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;


    @DisplayName("멤버의 팀은 팀의 프로퍼티를 조회하기 전까지 로딩이 지연된다")
    @Test
    void 멤버의_팀은_조회전까지_로딩이_지연된다() {
        var persistenceUnitUtil = entityManagerFactory.getPersistenceUnitUtil();
        var member = memberRepository.findById(1L).get();
        Assertions.assertThat(persistenceUnitUtil.isLoaded(member, "team")).isFalse();

    }

    @DisplayName("멤버의 팀은 팀의 프로퍼티를 조회하기 전까지 로딩이 지연된다")
    @Test
    void 팀의_프로퍼티를_get_하기_전_까진_로딩이_지연된다() {
        var persistenceUnitUtil = entityManagerFactory.getPersistenceUnitUtil();
        var member = memberRepository.findById(1L).get();
        var team = member.getTeam();
        Assertions.assertThat(persistenceUnitUtil.isLoaded(member, "team")).isFalse();
    }

    @DisplayName("멤버의 팀은 팀의 프로퍼티를 조회하면 로딩된다")
    @Test
    void 팀의_프로퍼티를_get_하면_로딩된다() {
        var persistenceUnitUtil = entityManagerFactory.getPersistenceUnitUtil();
        var member = memberRepository.findById(1L).get();
        var teamName = member.getTeam().getName();
        Assertions.assertThat(persistenceUnitUtil.isLoaded(member, "team")).isTrue();
    }

    @DisplayName("레이지로딩 대상이 이미 영속성 컨텍스트에 있다면 member.team 은 프록시 객체가 아니다")
    @Test
    void 레이지로딩_대상이_이미_영속성컨텍스트에_있다면_바로로딩된다() {
        var team = teamRepository.findById(1L).get();
        var persistenceUnitUtil = entityManagerFactory.getPersistenceUnitUtil();
        var member = memberRepository.findById(1L).get();
        Assertions.assertThat(persistenceUnitUtil.isLoaded(member, "team")).isTrue();
    }
}
