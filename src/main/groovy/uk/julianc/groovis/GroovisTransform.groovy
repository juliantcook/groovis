package uk.julianc.groovis

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import static org.codehaus.groovy.control.CompilePhase.CONVERSION

@GroovyASTTransformation(phase = CONVERSION)
class GroovisTransform implements ASTTransformation {
    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        GroovisBuilder.instance.add(source)
    }
}
