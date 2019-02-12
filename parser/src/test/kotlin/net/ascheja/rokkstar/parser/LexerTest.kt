package net.ascheja.rokkstar.parser

import net.ascheja.rokkstar.parser.Token.*
import org.junit.Assert.assertEquals
import org.junit.Test

class LexerTest {

    @Test
    fun `"'s +" is expanded to Space Word("is") Space`() {
        assertEquals(
            listOf(
                Word("Tommy"),
                Space,
                Word("is"),
                Space,
                Word("mean")
            ),
            tokenize("Tommy's \tmean")
        )
    }

    @Test
    fun `"'" stays part of word`() {
        assertEquals(
            listOf(
                Word("Rock"),
                Space,
                Word("'n'"),
                Space,
                Word("Roll")
            ),
            tokenize("Rock 'n' Roll")
        )
    }

    @Test
    fun `one liner is split into tokens properly`() {
        assertEquals(
            listOf(
                Word("Tommy"),
                Space,
                Word("was"),
                Space,
                Word("a"),
                Space,
                Word("lean"),
                Space,
                Word("mean"),
                Space,
                Word("wrecking"),
                Space,
                Word("machine"),
                Garbage('.')
            ),
            tokenize("Tommy was a lean mean wrecking machine.")
        )
    }

    @Test
    fun `two liner is split into tokens properly`() {
        assertEquals(
            listOf(
                Word("Tommy"),
                Space,
                Word("was"),
                Space,
                Word("a"),
                Space,
                Word("lean"),
                Space,
                Word("mean"),
                Space,
                Word("wrecking"),
                Space,
                Word("machine"),
                Garbage('.'),
                Eol,
                Word("Knock"),
                Space,
                Word("Tommy"),
                Space,
                Word("up"),
                Garbage(','),
                Space,
                Word("up"),
                Garbage(','),
                Space,
                Word("up"),
                Garbage('.')
            ),
            tokenize("Tommy was a lean mean wrecking machine.\nKnock Tommy up, up, up.")
        )
    }

    @Test
    fun `comment is one token`() {
        assertEquals(
            listOf(
                Word("Tommy"),
                Space,
                Word("was"),
                Space,
                Word("a"),
                Space,
                Word("lean"),
                Space,
                Word("mean"),
                Space,
                Word("wrecking"),
                Space,
                Word("machine"),
                Garbage('.'),
                Space,
                Space,
                Comment("initialises Tommy with the value 14487")
            ),
            tokenize("Tommy was a lean mean wrecking machine.  (initialises Tommy with the value 14487)")
        )
    }

    @Test
    fun `string literal is one token`() {
        assertEquals(
            listOf(
                Word("Put"),
                Space,
                StringLiteral("Hello San Francisco"),
                Space,
                Word("into"),
                Space,
                Word("the"),
                Space,
                Word("message")
            ),
            tokenize("Put \"Hello San Francisco\" into the message")
        )
    }

    @Test
    fun `escape chars are replaced in string literal`() {
        assertEquals("\n", tokenize(""""\n"""")[0].text)
        assertEquals("\r", tokenize(""""\r"""")[0].text)
        assertEquals("\t", tokenize(""""\t"""")[0].text)
        assertEquals("\\", tokenize(""""\\"""")[0].text)
    }

    @Test
    fun `escape chars with leading backslash are not replaced in string literal`() {
        assertEquals("\\n", tokenize(""""\\n"""")[0].text)
    }

    private fun tokenize(text: String): List<Token> = Lexer(text).tokens
}