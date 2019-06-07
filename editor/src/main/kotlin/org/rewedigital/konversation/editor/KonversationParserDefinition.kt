package org.rewedigital.konversation.editor

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.ParserDefinition.SpaceRequirements
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import org.rewedigital.konversation.editor.psi.KonversationElementType
import org.rewedigital.konversation.editor.psi.KonversationFile
import org.rewedigital.konversation.editor.psi.KonversationTypes

class KonversationParserDefinition : ParserDefinition {

    override fun createLexer(project: Project) = KonversationLexerAdapter()

    override fun getWhitespaceTokens() = WHITE_SPACES

    override fun getCommentTokens() = COMMENTS

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun createParser(project: Project) = KonversationParser()

    override fun getFileNodeType() = FILE

    override fun createFile(viewProvider: FileViewProvider) = KonversationFile(viewProvider)

    override fun spaceExistenceTypeBetweenTokens(left: ASTNode, right: ASTNode) = SpaceRequirements.MUST_LINE_BREAK

    override fun createElement(node: ASTNode): PsiElement = KonversationTypes.Factory.createElement(node)

    companion object {
        val WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE)
        val COMMENTS = TokenSet.create(KonversationElementType("KVS_TODO_COMMENT"))// FIXME KonversationTypes.COMMENT)

        val FILE = IFileElementType(KonversationLanguage.INSTANCE)
    }
}