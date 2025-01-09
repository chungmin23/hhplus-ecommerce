package kr.hhplus.be.server.domain.user;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.common.ErroMessages;
import kr.hhplus.be.server.infrastructure.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.module.ResolutionException;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    //최대 잔고 금액
    private static final int MAX_BALANCE_EXCEEDED = 1000000;
    
    @Transactional
    public User chargeBalance(Long userId, int amount){
        User user = userRepository.findById(userId).orElseThrow(()-> new IllegalArgumentException(ErroMessages.USER_NOT_FOUND));

        int updateBalance = user.getBalance() + amount;

        if(updateBalance > MAX_BALANCE_EXCEEDED){
            throw new IllegalArgumentException(ErroMessages.MAX_BALANCE_EXCEEDED);
        }

        user.setBalance(updateBalance);
        userRepository.save(user);

        return user;
    }

    @Transactional
    public User getBalance(Long userId){
        return userRepository.findById(userId).orElseThrow(()-> new IllegalArgumentException(ErroMessages.USER_NOT_FOUND));
    }

}
