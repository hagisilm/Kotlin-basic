package com.group.libraryapp.service.user

import com.group.libraryapp.controller.book.BookController
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.request.UserUpdateRequest
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserServiceTest @Autowired constructor(
     private val userRepository: UserRepository,
     private val userService: UserService,
     private val userLoanHistoryRepository: UserLoanHistoryRepository,
) {

    @BeforeEach
    fun init(){

    }

    @AfterEach
    fun finish(){
        println("끝끝")
        userRepository.deleteAll()
    }


    @Test
    @DisplayName("유저저장이 정상 동작")
    fun saveUserTest() {
        //given
        val request = UserCreateRequest("김병주",null)

        //when
        userService.saveUser(request)

        //then
        val result = userRepository.findAll()
        assertThat(result).hasSize(1)
        assertThat(result[0].name).isEqualTo("김병주")
        assertThat(result[0].age).isNull()

    }

    @Test
    @DisplayName("유저 조회가 정상 작동")
    fun getUsersTest() {
        //given
        userRepository.saveAll(listOf(
            User("A", 20),
            User("B", null),
        ))
        //when
        val results = userService.getUsers()

        //then
        assertThat(results).hasSize(2)
        assertThat(results).extracting("name").containsExactlyInAnyOrder("A","B")
        assertThat(results).extracting("age").containsExactlyInAnyOrder(20,null)
    }

    @Test
    @DisplayName("유저 업데이트가 정상 동작")
    fun updateUserNameTest(){
        //given
        val savedUser = userRepository.save(
            User(
                "rlaqudwn",
                25
            )
        )
        val request = UserUpdateRequest(savedUser.id!!,"김병주")
        //when
        userService.updateUserName(request)
        //then
        val result = userRepository.findAll()[0]
        assertThat(result).extracting("name").isEqualTo("김병주")
    }

    @Test
    @DisplayName("유저 삭제가 정상 동작")
    fun deleteUserTest() {
        //given
        val request = userRepository.save(
            User(
                "rlaqudwn",
                null
            )
        )
        //when
        userService.deleteUser(request.name)
        //then
        val result = userRepository.findAll()
        assertThat(result).hasSize(0)
    }

    @Test
    @DisplayName("사용자가 대출기록이 없는 경우")
    fun getUserLoanHistoriesTest1(){
        //given
        userRepository.save(User("김병주",null))
        //when
        val result = userService.getUserLoanHistories()
        //then
        assertThat(result).hasSize(1)
        assertThat(result[0].books).isEmpty()

    }

    @Test
    @DisplayName("사용자가 여러대출 기록이 있는 경우")
    fun getUserLoanHistoriesTest2(){
        //given
        val savedUser = userRepository.save(User("김병주",null))
        userLoanHistoryRepository.saveAll(listOf(
            UserLoanHistory.fixture(savedUser, "책1",UserLoanStatus.LOANED),
            UserLoanHistory.fixture(savedUser, "책2",UserLoanStatus.LOANED),
            UserLoanHistory.fixture(savedUser, "책3",UserLoanStatus.RETURNED),
        ))
        //when
        val result = userService.getUserLoanHistories()
        //then
        assertThat(result).hasSize(1)
        assertThat(result[0].books).extracting("name").containsExactlyInAnyOrder("책1","책2","책3")
        assertThat(result[0].books).extracting("isReturn").containsExactlyInAnyOrder(false,false,true)


    }
}