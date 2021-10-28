import java.util.Stack;

public class StackDemo {
    public static void main(String[] args){
        Stack<Node> stack = new Stack<>();
        Node n1 = new Node("n1");
        Node n2 = new Node("n2");
        Node n3 = new Node("n3");
        stack.push(n1);
        stack.push(n2);
        stack.push(n3);
        System.out.println(stack);
    }
}
