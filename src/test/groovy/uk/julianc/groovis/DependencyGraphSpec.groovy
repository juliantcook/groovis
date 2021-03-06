package uk.julianc.groovis

import org.codehaus.groovy.tools.ast.TransformTestHelper
import spock.lang.Specification

import static org.codehaus.groovy.control.CompilePhase.CONVERSION

class DependencyGraphSpec extends Specification {

    private static final boolean GENERATE_FILE = true

    def cleanup() {
        GroovisBuilder.instance.clear()
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
                '    "SomeService";\n' +
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
                '    "SomeService" -> "SomeOtherService";\n' +
                '    "SomeOtherService";\n' +
                '}\n'
    }

    def 'class with 2 children'() {
        given:
        input '''
        class Father {
            Daughter daughter
            Son son
        }

        class Daughter {}
        class Son {}
'''

        expect:
        output ==
                'digraph {\n' +
                '    "Father" -> "Daughter";\n' +
                '    "Father" -> "Son";\n' +
                '    "Daughter";\n' +
                '    "Son";\n' +
                '}\n'
    }

    def 'circular relation'() {
        given:
        input '''
        class Foo {
            Bar bar
        }

        class Bar {
            Poop poop
        }

        class Poop {
            Foo foo
        }
'''

        expect:
        output ==
                'digraph {\n' +
                '    "Foo" -> "Bar";\n' +
                '    "Bar" -> "Poop";\n' +
                '    "Poop" -> "Foo";\n' +
                '}\n'
    }

    def 'ignore enums'() {
        given:
        input '''
        class Foo {
            Bar bar
        }

        enum Bar {
            POO
        }
'''

        expect:
        output ==
                'digraph {\n' +
                '    "Foo";\n' +
                '}\n'
    }

    def 'options: dont include orphans'() {
        given:
        def opts = new Options(includeOrphans: false)

        and:
        input '''
        class Father {
            Son son
        }

        class Son {}

        class Orphan {}
'''

        expect:
        getOutput(opts) ==
                'digraph {\n' +
                '    "Father" -> "Son";\n' +
                '}\n'
    }

    private input(String input) {
        def transform = new GroovisTransform()
        new TransformTestHelper(transform, CONVERSION).parse input
    }

    private String getOutput(Options options = new Options()) {
        def out = GroovisBuilder.instance.generate(options)
        if (GENERATE_FILE) {
            generateFile(out)
        }
        out
    }

    private void generateFile(String out) {
        generateImage(writeDotFile(out))
    }

    private File writeDotFile(String dotContents) {
        def dir = new File("build/graphs/")
        dir.mkdirs()
        def fileName = "${specificationContext.currentFeature.name.replace(' ', '_')}.dot"
        new File(dir, fileName) << dotContents
    }

    private generateImage(File file) {
        "dot -O -T png ${file.path}".execute()
    }
}
