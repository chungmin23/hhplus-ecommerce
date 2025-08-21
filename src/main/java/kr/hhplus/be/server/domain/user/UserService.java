package kr.hhplus.be.server.domain.user;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.interfaces.exception.ErrorMessages;
import kr.hhplus.be.server.infrastructure.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    //최대 잔고 금액
    private static final int MAX_BALANCE_EXCEEDED = 1000000;

    // 사용자 조회
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.USER_NOT_FOUND));
    }

    // 잔고 충전
    @Transactional
    public User chargeBalance(Long userId, int amount){
        User user = userRepository.findUserForUpdate(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.USER_NOT_FOUND));

        int updateBalance = user.getBalance() + amount;

        if(updateBalance > MAX_BALANCE_EXCEEDED){
            throw new IllegalArgumentException(ErrorMessages.MAX_BALANCE_EXCEEDED);
        }

        user.setBalance(updateBalance);
        userRepository.save(user);

        return user;
    }

    // 잔고 조회
    public User getBalance(Long userId){
        return userRepository.findById(userId).orElseThrow(()-> new IllegalArgumentException(ErrorMessages.USER_NOT_FOUND));
    }

    //잔고 감소
    @Transactional
    public User deductBalance(Long userId, int amount) {
        // 사용자 조회 (비관적 락 사용)
        User user = userRepository.findUserForUpdate(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.USER_NOT_FOUND));
        
        // 잔액 확인
        if (user.getBalance() < amount) {
            throw new IllegalArgumentException(ErrorMessages.INSUFFICIENT_BALANCE);
        }
        
        //잔액 차감
        user.setBalance(user.getBalance() - amount);
        return userRepository.save(user);
    }

}
