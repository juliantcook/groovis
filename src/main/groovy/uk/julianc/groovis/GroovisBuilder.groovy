package uk.julianc.groovis

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.control.SourceUnit

@Singleton
class GroovisBuilder {

    Map composition = [:]

    void add(SourceUnit sourceUnit) {
        sourceUnit.AST.classes.each { ClassNode classNode ->
            def parent = existingNode(classNode.name)
            if (parent == null) parent = composition[classNode.name] = [:]
            classNode.fields.each { FieldNode fieldNode ->
                parent[fieldNode.type.name] = [:]
            }
        }
    }

    private Map existingNode(String name, Map<String, Map> nodes = composition) {
        if (nodes) {
            nodes[name] != null ? nodes[name] : nodes.findResult { k, children ->
                existingNode(name, children)
            }
        }
    }

    String generate() {
        def out = 'digraph {\n'
        composition.each { String parent, Map children ->
            if (children) {
                children.each { String child, _ ->
                    out += "    $parent -> $child;\n"
                }
            } else {
                out += "    $parent;\n"
            }
        }
        out + '}\n'
    }
}
