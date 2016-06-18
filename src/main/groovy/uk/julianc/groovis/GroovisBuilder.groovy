package uk.julianc.groovis

import org.codehaus.groovy.control.SourceUnit

@Singleton
class GroovisBuilder {

    Map composition = [:]

    void add(SourceUnit sourceUnit) {
        composition[sourceUnit.AST.classes[0].name] = [:]
    }

    String generate() {
        def out = 'digraph {\n'
        composition.each { parent, child ->
            out += "    $parent;\n"
        }
        out + '}\n'
    }
}
