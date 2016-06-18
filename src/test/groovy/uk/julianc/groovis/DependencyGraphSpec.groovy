package uk.julianc.groovis

import org.codehaus.groovy.tools.ast.TransformTestHelper
import spock.lang.Specification

import static org.codehaus.groovy.control.CompilePhase.CONVERSION

class DependencyGraphSpec extends Specification {

    def 'single class with no fields produces single node'() {
        given:
        def transform = new GroovisTransform()
        def clazz = new TransformTestHelper(transform, CONVERSION).parse '''
        class SomeService {
            def someMethod() {}
        }
'''

        expect:
        GroovisBuilder.instance.generate() ==
            'digraph {\n' +
            '    SomeService;\n' +
            '}\n'
    }

}
