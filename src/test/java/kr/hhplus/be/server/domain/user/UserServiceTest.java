package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.interfaces.exception.ErroMessages;
import kr.hhplus.be.server.infrastructure.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @DisplayName("포인트 충전 성공 확인")
    @Test
    void pointCharge_successs(){
        //given
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .name("철수")
                .balance(1000)
                .build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //when
        userService.chargeBalance(userId, 500);

        //Then
        Mockito.verify(userRepository).save(Mockito.argThat(savedUser->
                savedUser.getBalance() == 1500
                ));
    }


    @DisplayName("포인트 조회 성공 확인")
    @Test
    void getBalance_successs(){
        //Given
        Long useId = 1L;
        User user = User.builder()
                .id(useId)
                .name("철수")
                .balance(1000)
                .build();

        Mockito.when(userRepository.findById(useId)).thenReturn(Optional.of(user));

        //when
        User user2 = userService.getBalance(useId);

        //then
        Assertions.assertEquals(1000, user2.getBalance());
    }

    @DisplayName("사용자가 있는지 테스트 합니다")
    @Test
    void userNotFoundTest(){
        //Given
        Long userId = 1L;


        //when & then
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());


        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> userService.chargeBalance(userId, 500)
        );

        Assertions.assertEquals(ErroMessages.USER_NOT_FOUND, exception.getMessage());

    }

    @DisplayName("금액을 초과하는지 확인합니다")
    @Test
    void chargeBalance_limitExceeded(){
        //Given
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .name("철수")
                .balance(999500)
                .build();

        //when & then
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));


        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> userService.chargeBalance(userId, 1000)
        );


        Assertions.assertEquals(ErroMessages.MAX_BALANCE_EXCEEDED, exception.getMessage());
    }

}