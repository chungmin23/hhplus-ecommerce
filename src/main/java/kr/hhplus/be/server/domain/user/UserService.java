package kr.hhplus.be.server.domain.user;

import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import kr.hhplus.be.server.interfaces.exception.ErroMessages;
import kr.hhplus.be.server.infrastructure.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    //최대 잔고 금액
    private static final int MAX_BALANCE_EXCEEDED = 1000000;

    // 비관적 락 추가
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErroMessages.USER_NOT_FOUND));
    }

    // 잔고 충전
    @Transactional
    public User chargeBalance(Long userId, int amount){
        User user = userRepository.findUserForUpdate(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        int updateBalance = user.getBalance() + amount;

        if(updateBalance > MAX_BALANCE_EXCEEDED){
            throw new IllegalArgumentException(ErroMessages.MAX_BALANCE_EXCEEDED);
        }

        user.setBalance(updateBalance);
        userRepository.save(user);

        return user;
    }

    // 잔고 조회
    public User getBalance(Long userId){
        return userRepository.findById(userId).orElseThrow(()-> new IllegalArgumentException(ErroMessages.USER_NOT_FOUND));
    }

    //잔고 감소
    @Transactional
    public User deductBalance(Long userId, int amount) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErroMessages.USER_NOT_FOUND));
        
        // 잔액 확인
        if (user.getBalance() < amount) {
            throw new IllegalArgumentException(ErroMessages.INSUFFICIENT_BALANCE);
        }
        
        //잔액 차감
        user.setBalance(user.getBalance() - amount);
        return userRepository.save(user);
    }

}
