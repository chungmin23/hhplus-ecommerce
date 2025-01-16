package kr.hhplus.be.server.domain.user;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.infrastructure.user.UserRepository;
import kr.hhplus.be.server.interfaces.exception.ErroMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 사용자 생성
        testUser = User.builder()
                .name("Test User")
                .balance(5000)
                .build();
        userRepository.save(testUser);
    }

    @DisplayName("잔고 충전, 차감, 조회 통합 테스트")
    @Test
    void balanceIntegrationTest() {
        // Given
        int initialBalance = testUser.getBalance();
        int chargeAmount = 2000;
        int deductAmount = 1500;

        // Step 1: 잔고 충전
        User chargedUser = userService.chargeBalance(testUser.getId(), chargeAmount);
        Assertions.assertEquals(initialBalance + chargeAmount, chargedUser.getBalance());

        // Step 2: 잔고 차감
        User deductedUser = userService.deductBalance(testUser.getId(), deductAmount);
        Assertions.assertEquals(initialBalance + chargeAmount - deductAmount, deductedUser.getBalance());

        // Step 3: 잔고 조회
        User finalUser = userService.getBalance(testUser.getId());
        Assertions.assertEquals(initialBalance + chargeAmount - deductAmount, finalUser.getBalance());
    }
}
