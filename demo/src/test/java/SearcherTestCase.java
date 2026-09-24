import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import com.uem.Searcher;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SearcherTestCase {

    public Searcher searcher;

    @BeforeEach
    public void setUp() {
        searcher = new Searcher();
    }

    @Test
    void searchWordExistsTest() {
        List<String> list = List.of("apple", "banana", "cherry");

        assertEquals(true, searcher.searchWord("banana", list));
    }

    @Test
    void searchWordNotExistsTest() {
        List<String> list = List.of("apple", "banana", "cherry");

        assertEquals(false, searcher.searchWord("grape", list));
    }

    @Test
    void getWordByIndexTest() {
        List<String> list = List.of("apple", "banana", "cherry");

        assertEquals("banana", searcher.getWordByIndex(list, 1));
    }

    @Test
    void getWordByIndexOutOfBoundsTest() {
        List<String> list = List.of("apple", "banana", "cherry");

        assertEquals(null, searcher.getWordByIndex(list, 5));
    }

    @Test
    void getWordByIndexNegativeTest() {
        List<String> list = List.of("apple", "banana", "cherry");

        assertEquals(null, searcher.getWordByIndex(list, -1));
    }

    @Test
    void searchByPrefixTest() {
        List<String> list = List.of("apple", "banana", "cherry", "apricot");
        List<String> expected = List.of("apple", "apricot");

        assertEquals(expected, searcher.searchByPrefix("ap", list));
    }

    @Test
    void searchByPrefixNoMatchTest() {
        List<String> list = List.of("apple", "banana", "cherry");
        List<String> expected = List.of();

        assertEquals(expected, searcher.searchByPrefix("gr", list));
    }

    @Test
    void filterByKeywordTest() {
        List<String> list = List.of(
                "apple pie",
                "banana split",
                "cherry tart",
                "apple turnover");

        List<String> expected = List.of(
                "apple pie",
                "apple turnover");

        assertEquals(expected, searcher.filterByKeyword("apple", list));
    }

    @Test
    void filterByKeywordNoMatchTest() {
        List<String> list = List.of(
                "apple pie",
                "banana split",
                "cherry tart");

        List<String> expected = List.of();

        assertEquals(expected, searcher.filterByKeyword("grape", list));
    }

    @Test
    void searchExactPhraseTest() {
        List<String> list = List.of("apple", "banana", "cherry");

        assertEquals(true, searcher.searchExactPhrase("banana", list));
    }
}