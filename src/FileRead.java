import java.io.File;
import java.util.Scanner;
import java.util.Stack;

public class FileRead {

    public static int spaceCount(String s){
        int sc =0;
        int i;
        for(i=0;i<s.length();i++){
            if(!Character.isWhitespace(s.charAt(i)))
                break;
            sc++;

        }
        return sc;
    }

    public static Node loadTreeFromFile(String filePath){
        System.out.println("loadTreeFromFile filename: " + filePath);
        Node node = null;
        Node prevNode = null;
        Node root = new Node("main root");
        Node currentNode = root;
        prevNode = currentNode;
        Node parentNode = root;
        try {
            Stack<Node> stack = new Stack<Node>();
            Scanner in = new Scanner(new File(filePath));
            int prevsc = -1;
            while (in.hasNext()) {
                String l = in.nextLine();
                int sc = spaceCount(l);
                String title = l.trim();
                System.out.println("psc:" + prevsc + ", sc:" + sc + ", title:" + title);
                node = new Node(title);
                if (prevsc < sc) {
                    stack.push(currentNode);
                    prevNode.addChild(node);
                    currentNode = prevNode;
                } else if (prevsc == sc) {
                    currentNode.addChild(node);
                } else { //prevsc > sc -> pop
                    currentNode = stack.pop();
                    currentNode.addChild(node);
                }
                prevsc = sc;
                prevNode = node;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
    }

    public static void main(String[] args) {
        //String filename = "D:\\git2\\tree_cli\\src\\test2.tab.txt";
        //String filename = "D:\\git2\\tree_cli\\src\\test.tab.txt";
        String filePath ="D:\\git2\\tree_cli\\src\\test.tab.txt";
        Node root = loadTreeFromFile(filePath);
        if(root!=null){
            root.print();
        }
    }
}
