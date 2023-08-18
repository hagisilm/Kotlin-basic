package com.group.libraryapp.service.book

import com.group.libraryapp.domain.book.Book
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.book.BookType
import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import com.group.libraryapp.dto.book.response.BookStatResponse
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.hibernate.query.criteria.internal.expression.function.AggregationFunction.COUNT
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BookServiceTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val bookRepository: BookRepository,
    private val bookService: BookService,
    private val userLoanHistoryRepository: UserLoanHistoryRepository
) {

    @AfterEach
    fun finish() {
        bookRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    @DisplayName("책 저장 정상 가능")
    fun saveBookTest() {
        //given
        val request = BookRequest("c",BookType.COMPUTER)
        //when
        bookService.saveBook(request)
        //then
        val result = bookRepository.findAll()
        assertThat(result).hasSize(1)
        assertThat(result[0].name).isEqualTo("c")
        assertThat(result[0].type).isEqualTo(BookType.COMPUTER)

    }

    @Test
    @DisplayName("책 대출 정상 가능")
    fun loanBookTest() {
        //given
        bookRepository.save(Book.fixture("a"))
        val savedUser = userRepository.save(User("김병주", null))
        val request = BookLoanRequest("김병주","a")

        //when
        bookService.loanBook(request)

        //then
        val result = userLoanHistoryRepository.findAll()
        assertThat(result).hasSize(1)
        assertThat(result[0].bookName).isEqualTo("a")
        assertThat(result[0].user.id).isEqualTo(savedUser.id)
        assertThat(result[0].status).isEqualTo(UserLoanStatus.LOANED)
    }

    @Test
    @DisplayName("책 대출 실패")
    fun loanBookFailTest() {
        //given
        bookRepository.save(Book.fixture("a"))
        val savedUser = userRepository.save(User("김병주", null))
        userLoanHistoryRepository.save(UserLoanHistory(savedUser,"a"))
        val request = BookLoanRequest("김병주","a")
        //when & then
        val message = assertThrows<IllegalArgumentException> {
            bookService.loanBook(request)
        }.message
        assertThat(message).isEqualTo("진작 대출되어 있는 책입니다")
    }

    @Test
    @DisplayName("책 반납")
    fun returnBookTest(){
        //given
        bookRepository.save(Book.fixture("a"))
        val savedUser = userRepository.save(User("김병주", null))
        userLoanHistoryRepository.save(UserLoanHistory.fixture(savedUser,"a"))
        val request = BookReturnRequest("김병주","a")
        //when
        bookService.returnBook(request)
        //then
        val result = userLoanHistoryRepository.findAll()
        assertThat(result[0].status).isEqualTo(UserLoanStatus.RETURNED)
    }

    @Test
    @DisplayName("책 대여 권수를 정상 확인한다.")
    fun countLoanedBookTest(){
        //given
        val savedUser = userRepository.save(User("김병주",null))
        userLoanHistoryRepository.saveAll(listOf(
            UserLoanHistory.fixture(savedUser, "A"),
            UserLoanHistory.fixture(savedUser, "B",UserLoanStatus.RETURNED),
            UserLoanHistory.fixture(savedUser, "C",UserLoanStatus.RETURNED),
        ))
        //when
        val result = bookService.countLoanedBook()
        //then
        assertThat(result).isEqualTo(1)
    }

    @Test
    @DisplayName("분야별 책 권수를 정상 확인한다.")
    fun getBookStatics(){
        //given
        bookRepository.saveAll(listOf(
            Book.fixture("A", BookType.COMPUTER),
            Book.fixture("B",BookType.COMPUTER),
            Book.fixture("C",BookType.SCIENCE),
        ))
        //when
        val results = bookService.getBookStatics()
        //then
        assertThat(results).hasSize(2)
        assertCount(results, BookType.COMPUTER, 2L)
        assertCount(results, BookType.SCIENCE,1L)

//        val computerDto = results.first { result -> result.type == BookType.COMPUTER}
//        assertThat(computerDto.count).isEqualTo(2)
//
//        val scienceDto = results.first {result -> result.type == BookType.SCIENCE}
//        assertThat(computerDto.count).isEqualTo(1)
    }

    private fun assertCount(results: List<BookStatResponse>,type: BookType, count: Long){
        assertThat(results.first {result -> result.type == type}.count).isEqualTo(count)
    }

}