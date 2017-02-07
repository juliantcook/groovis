package uk.julianc.groovis

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import static org.codehaus.groovy.control.CompilePhase.CANONICALIZATION

@GroovyASTTransformation(phase = CANONICALIZATION)
class GroovisTransformFinaliser implements ASTTransformation {

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        if (!GroovisBuilder.instance.generated) {
            println "Groovis:"
            println GroovisBuilder.instance.generate()
        }
    }
}
