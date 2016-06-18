package uk.julianc.groovis

import org.codehaus.groovy.tools.ast.TransformTestHelper
import spock.lang.Specification

import static org.codehaus.groovy.control.CompilePhase.CONVERSION

class DependencyGraphSpec extends Specification {

    def cleanup() {
        def file = writeDotFile()
        generateImage(file)
    }

    private File writeDotFile() {
        def dir = new File("build/graphs/")
        dir.mkdirs()
        def fileName = "${specificationContext.currentFeature.name.replace(' ', '_')}.dot"
        new File(dir, fileName) << GroovisBuilder.instance.generate()
    }

    private generateImage(File file) {
        "dot -O -T png ${file.path}".execute()
    }

    def 'single class with no fields produces single node'() {
        given:
        input '''
        class SomeService {
            def someMethod() {}
        }
'''

        expect:
        output ==
                'digraph {\n' +
                '    SomeService;\n' +
                '}\n'
    }

    def '2 classes can be singly linked'() {
        given:
        input '''
        class SomeService {
            SomeOtherService someOtherService
        }

        class SomeOtherService {}
'''

        expect:
        output ==
                'digraph {\n' +
                '    SomeService -> SomeOtherService;\n' +
                '}\n'
    }

    private input(String input) {
        def transform = new GroovisTransform()
        new TransformTestHelper(transform, CONVERSION).parse input
    }

    private String getOutput() {
        GroovisBuilder.instance.generate()
    }
}
