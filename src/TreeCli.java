

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class Node {
    static int nodeIdSeq=0;
    static int SPACES_FOR_LEVEL = 4;
    int id;
    String title; //like title
    boolean competed;
    boolean collapse; // collapse rendering of viewing purpose only
    String notes; //notes for node
//    int level=0;

    List<Node> childrenList = new LinkedList<Node>();
    Map<Integer, Node> childrenMap = new HashMap<Integer, Node>();

    Node parent = null;

    public static void addToTreeNodeMap(Node node){
        if(node == null){
            return;
        }
        TreeCli.nodeMap.put(node.id, node);
    }

    public Node(){
        this.id = nodeIdSeq++;
        this.title = "default text";
        addToTreeNodeMap(this);
    }
    public Node(String text){
        this();
        this.title = text;
    }
    public Node(int id, String text) {
        this.id = id;
        this.title = text;
        addToTreeNodeMap(this);
    }
    public Node(String text, Node parent){
        this(text);
        this.parent = parent;
    }

    public Node addChild(String text){
        Node child = new Node(text);
        addChild(child);
//        childrenList.add(child);
        return child;
    }

    public void addChild(Node child) {
        childrenMap.put(child.id, child);
        childrenList.add(child);
        child.setParent(this);
    }

    public void setParent(Node parent){
        this.parent = parent;
    }
    public Node addSibling(String text ){
       if(this.parent ==null){
           System.out.println("can not sibling as this root node");// new feature later
           return null;
       }
       Node newNode = new Node(text);
       int currentNodeIndex = this.parent.childrenList.indexOf(this);

       if(currentNodeIndex !=-1){
           this.parent.childrenList.add(currentNodeIndex+1, newNode);
           this.parent.childrenMap.put(newNode.id, newNode);
           newNode.setParent(this.parent);
       }

       return newNode;
    }
    public void removeChild(Integer childId) {
        childrenMap.remove(childId);
    }

    public static void printSpaces(int numSpaces){
        for(int i=0;i<numSpaces;i++){
            System.out.print(' ');
        }
    }

    public static void printSpaceForLevel(int level){
        int numSpaces = level * SPACES_FOR_LEVEL;
        printSpaces(numSpaces);
    }
    public void print() {
        print(this, 0);
        System.out.println(); // give extra space for completion
    }

    public void printNoChildren(){
        printNoChildren(this);
    }

    public static  void printNoChildren(Node node){
        System.out.println("(id:" + node.id + ", text: " + node.title +")");
    }

    public static String getNodePrintPrefix(Node node){
        String p = "";
        if(node == null){
            return p;
        }
        if(node == TreeCli.currentNode){
            p = p + "c";
        }
        if(node == TreeCli.zoomNode){
            p +=  "z";
        }
        if(node == TreeCli.root){
            p += "r";
        }
        return p;
    }

    public static void print(Node node, int level){
        printSpaceForLevel(level);
        String prefix = getNodePrintPrefix(node);
        System.out.println(prefix + "(id:" + node.id + ", text: " + node.title +")");
        if(node.childrenList.isEmpty())
            return;
        if(node.collapse) // collapse means -> dont show children
            return;
        for(Node child: node.childrenList){
            print(child, level+1);
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void deleteChild(Node node) {
        int ci = childrenList.indexOf(node);
        if(ci != -1){
            childrenList.remove(node);
            childrenMap.remove(node.id);
        }else{
            System.out.println("can not child node " + node.id + " from parent id: " + id);
        }
    }

    public List<Node> search(String input) {

        List<Node> searchList = new LinkedList<Node>();
        search(this, input, searchList);
        return searchList;
    }

    public static void search(Node node, String input, List<Node> searchList){
        if(node == null){
            return;
        }
        if(node.title.toUpperCase().contains(input.toUpperCase())){
            searchList.add(node);
        }
        if(node.childrenList.isEmpty()){
            return;
        }
        for(Node child: node.childrenList){
            search(child, input, searchList);
        }
    }

    public void printWithNoteCompleteNoChildren() {
        System.out.println("(id:" + this.id + ", title:" + this.title
                + ", complete: " + this.competed
                + ", notes: " + this.notes +")");
    }
}

public class TreeCli {
    static Scanner scanner = null;
    static Map<Integer, Node> nodeMap = new HashMap<Integer, Node>();
    static Node root = new Node("baaji laal");
    static Node zoomNode = root;
    static Node currentNode = root;
    static String text = null;
    static Node newNode = null;


    public static void main(String[] args){
        System.out.println("TreeCli");

        // initial test data

        String testFilepath = "D:\\git2\\tree_cli\\src\\test.tab.txt";;
        root = loadTreeFromFile(testFilepath);
        setZoomNode(root);
        setCurrentNode(root);
        scanner = new Scanner(System.in);

        while(true) {
//            System.out.println("***Root***");
//            root.print();
            int num = getCommandNum();

            switch (num) {
                case 0: //exit
                    System.out.println("Exiting");
                    System.exit(0);
                    break;
                case 1: // display
                    System.out.println("current node:");
                    currentNode.printWithNoteCompleteNoChildren();
                    break;
                case 2: // add child
                    System.out.print("Add child-> enter text:");
//                    scanner.nextLine();// will eat new line enter during entering menu number<enter>
                    text = scanner.nextLine();
                    newNode = currentNode.addChild(text);
                    currentNode = newNode;
                    break;
                case 3: //add sibling
                    System.out.print("adding sibling->");
                    System.out.print("enter text:");
//                    scanner.nextLine();// will eat new line enter during entering menu number<enter>
                    text = scanner.nextLine();
                    newNode = currentNode.addSibling(text);
//                    currentNode = newNode;
                    setCurrentNode(newNode);
                    setZoomNode(newNode.parent);
                    break;
                case 4:// delete current node
                    deleteCurrentNode();
                    break;
                case 5: //edit node
                    editCurrentNode();
                    break;
                case 6: //display root
                    root.print();
                    break;
                case 7: // edit text current node
                    System.out.print("Edit current node -> enter text:");
//                    scanner.nextLine();// will eat new line enter during entering menu number<enter>
                    text = scanner.nextLine();
                    currentNode.setTitle(text);
                    break;
                case 8: //zoom in
                    System.out.println("zoom in");
                    zoomIn();
                    break;
                case 9: // zoom out -> migh not be needed, lets later
//                    System.out.println("zoom out");
                    zoomOut();
                    break;
                case 10: // zoom node print
                    zoomNodePrint();
                    break;

                case 11: //search
                    search();
                    break;

                case 12: //add note
                    viewNote();
                    break;
                case 13: // edit note
                    editNote(); //TODO
                    break;
                case 14: //toggle complete
                    toggleComplete();
                    break;
                case 15://navigation go to node
//                    navigation();
                    navigationGotoIdHandler();
                    zoomNode.print();
                    break;
                case 16: // toggle collapse
                    toggleCollapse();

                case 17: // navigation up or  u key
                    navigationUp();
                    zoomNode.print();
                    break;

                case 18: // navigation down or d key
                    navigationDown();
                    zoomNode.print();
                    break;

                case 19: //load from file
                    loadFromFileCaseHandler();
                    break;
                case 20: // help
                    helpHanlder();
                    break;
                case 21: // export e, save s
                    exportHandler();
                    break;
                default:
                    System.out.println("invalid or not implemented yet");
                    break;

            }
        }
    }

    private static void exportHandler() {
        System.out.println(">export file path: ");
        String filePath = null;
//        scanner.nextLine();
//        scanner.nextLine();
        filePath = scanner.nextLine();
        exportToFile(filePath);
    }

    public static String getWord(String prompt, String requireMessage) {
        String word = null;
        int retry = 3;
        for(retry=3;retry>0; retry--) {
            word = scanner.nextLine();
            if (word == null || word.trim().isEmpty()) {
                System.out.println(requireMessage);
                continue;
            }
        }
        return word;
    }

    private static void exportToFile(String filePath) {
        System.out.println("> export root fo file path: " + filePath);
        if(filePath == null || filePath.trim().isEmpty()){
            System.out.println("file path is empty , can not save to empty file name");
            return ;
        }
        FileWriter fw =  null;
        try {
            fw = new FileWriter(new File(filePath));

            exportToFileWriter(root,0, fw);
            fw.close();
            System.out.println("****Exported success *** ");
        }catch(Exception e){
            e.printStackTrace();;
        }
    }

    private static void exportToFileWriter(Node node, int level,  FileWriter fw) {
        try {
            // print spaces
            writeLevelSpacesToFileWriter(level, fw);
            fw.write(node.title);
            fw.write('\n');
            for(Node child : node.childrenList){
                exportToFileWriter(child, level+1, fw);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void writeLevelSpacesToFileWriter(int level, FileWriter fw) throws IOException {
        int spaces = level * Node.SPACES_FOR_LEVEL;
        for(int i=0;i<spaces;i++){
            fw.write(' ');
        }
    }

    private static void helpHanlder() {
        System.out.println("Help Page");
        System.out.println("Main Menu: ("
            + " 0 exit "
            + " 1.display current node"
            + " 2.add child (c)"
            + " 3. add sibling(s)"
            + " 4. delete node "
            + " 5. edit node "
            + "\n"
            + " 6. dispaly root(r) "
            + " 7. edit text current node(t) "
            + " 8. zoom in(z) "
            + " 9. zoom out(o)"
            + " 10. zoom node print"
            + "\n"
            + " 11. search (f)"
            + "12. view note "
            + " 13. edit note(n) "
            + " 14. toggle complete(complete) "
            + " 15 navigation to node id(g)"
            + " 16 toggle collapse(v or visible) "
            + " 17 navigation up(u)"
            + " 18 navigation down(d)"
            + " 19 load from file (l)"
            + " )");
    }

    private static void loadFromFileCaseHandler() {
        System.out.println("Load from file -> file: ");
        // D:\git2\tree_cli\src\test.tab.txt
        String filePath = scanner.nextLine();
        root = loadTreeFromFile(filePath);
        setZoomNode(root);
        setCurrentNode(root);
        root.print();
    }

    public static int getCommandNum(){
        String cmd = null;
        System.out.print(">");
        cmd = scanner.nextLine();
        int num = -1;
        if(cmd.equals("help") || cmd.equals("h")){
            num = 20;
        } else if(cmd.equals("c")){ //add child
            num = 2;
        } else if(cmd.equals("s")){ //add sibling
             num = 3;
        } else if(cmd.equals("r")){ // display root
            num = 6;
        } else if(cmd.equals("t")){ // edit text/title node
            num = 7;
        } else if(cmd.equals("z")){ // zoom in
             num = 8;
        } else if(cmd.equals("o")) { // zoom out
            num =9;
        } else if(cmd.equals("f")){ //search or find
            num = 11;
        } else if(cmd.equals("n")){ // edit note
            num = 12;
        } else if(cmd.equals("complete")) { //toggle complete/done
            num = 14;
        } else if(cmd.equals("v")){ // toggle collapse /visible
            num = 16;
        } else if(cmd.equals("u")){
            num = 17;
        } else if(cmd.equals("d")){
            num = 18;
        } else if(cmd.equals("l")) { // load from file
            num = 19;
        } else if(cmd.equals("export") || cmd.equals("e") || cmd.equals("s") || cmd.equals("save") ){
            num = 21;
        } else {
            try {
                num = Integer.parseInt(cmd);
            }catch(Exception e){
                System.out.println(e);
            }
        }
        return num;
    }

    static void toggleCollapse() {
        currentNode.collapse = !currentNode.collapse;
    }

    private static void navigationGotoIdHandler() {
        int id;
        System.out.print("enter id(num):");
        id = scanner.nextInt();
        setCurrentNodeToNodeId(id);
    }

    static void navigationUp(){
        /**
         * generally it should following traversal root, left, right
         *
         * if current node have previous sibling
         *      if the previous sibling is either collapse or does not have children then
         *          curernt node is previos sibling
     *          if previuos sibling have children  //todo need improve
         *          for now set previous sibling last children if last children is collapse or does not have children
     *              if previous sibling last children have children or not collapse then
*          else if curent node dees not have previous sibling then
         *           set current node to parent
         *
         */

        System.out.println("navigation up");
        boolean haveParent = currentNode.parent != null;
        Node parent = currentNode.parent;
        boolean havePrevSibling = haveParent ? parent.childrenList.indexOf(currentNode) != 0 : false;

        if(!havePrevSibling){
            if(parent!= null) {
                setCurrentNode(currentNode.parent);
            }
        }else {
            int ci = parent.childrenList.indexOf(currentNode);
            Node prevSibling = parent.childrenList.get(ci-1);
            // previous sibling collapse or does not have children then set current node to previous sibling
            if(prevSibling.collapse || prevSibling.childrenList.isEmpty()) {
                setCurrentNode(prevSibling);
            }
            // previsious sibling have children, not collapse -> then
                // go to the last children
            else if(!prevSibling.collapse && !prevSibling.childrenList.isEmpty()){
                Node lastChild = null;

                Node node = prevSibling;

                while(true) {
                    lastChild = node.childrenList.get(prevSibling.childrenList.size() - 1);

                    if (lastChild.collapse || lastChild.childrenList.isEmpty()) {
                        setCurrentNode(lastChild);
                        break;
                    } else {
                        node = lastChild;
                    }
                }
            }
        }
    }

    static void navigationDown() {
        /**
         * generally it should following traversing of root, left, right
         *
         * if current node is collapse then
         *      if current node next sibling node go there , set current node
         *      if current node does not have next sibling  then
         *          go parent //todo , need improvise
     *     else // if current node is not collapse
         *     if current node does not have children then
         *          if current node have next sibling then
         *              go there
     *              else
         *              go to parent // todo improve
         *
 *              else if current node is have children then
 *                  go to first children
         */
        System.out.println("navigation down");
        boolean isCollapse = currentNode.collapse;
        boolean haveChildren = !currentNode.childrenList.isEmpty();
        boolean haveParent = currentNode.parent != null;
        Node parent = currentNode.parent;
        boolean haveNextSibling = haveParent ? (parent.childrenList.indexOf(currentNode) < parent.childrenList.size()-1): false;
        if((isCollapse ||! haveChildren ) && haveNextSibling){ // have next sbiling, does not have children or collapse then set next sibling as current node
            int ci = parent.childrenList.indexOf(currentNode);
            Node nextSibling = parent.childrenList.get(ci+1);
            setZoomNode(currentNode.parent);
            setCurrentNode(nextSibling);
        }
        else if(!isCollapse && haveChildren) { //have children, not collapse then first child be current node, zoom node current node
            Node oldCurrentNode = currentNode;
            setZoomNode(oldCurrentNode);
            Node firstChild = currentNode.childrenList.get(0);
            setCurrentNode(firstChild);
        }else if((!haveChildren || isCollapse) && !haveNextSibling){ // (collapse or no children) and does not have next sibling then parent as current node, zoom node parnet.parent
            // TODO not correct; improve further  -> follow traverse of l root right
            Node parentNextSibling = null;

            while(parent !=null && parent.parent !=null) {
                int pi = parent.parent.childrenList.indexOf(parent);
                if (pi < parent.parent.childrenList.size() - 1) {
                    currentNode = parent.parent.childrenList.get(pi + 1);
                    setCurrentNode(currentNode);
                    if(currentNode.parent != null){
                        setZoomNode(currentNode.parent);
                    }else{
                        setZoomNode(currentNode);
                    }
                    break;
                } else {
                    // parent is last child of parent.parent
                    parent = parent.parent;
                }
            }

        }
    }

    private static void setCurrentNodeToNodeId(int id) {
        if(id <0){
            System.out.println("invalid node id");
            return;
        }
        // todo id to node map for all tree
        Node node = TreeCli.nodeMap.get(id);
        if(node == null){
            System.out.println("no node exist with id :" + id);
            return;
        }
        setCurrentNode(node);
//        setZoomNode(node); todo  is old zoom node is still relevant
        //temporry set zoom node to current node parent if exist
        setZoomNode(node.parent);
    }

    static void setZoomNode(Node node) {
        if(node == null){
            System.out.println("node is null");
            return;
        }
        zoomNode = node;
    }

    private static void setCurrentNode(Node node) {
        currentNode = node;
    }

    private static void zoomOut() {
        System.out.println("zoom out");
        if(currentNode != null && currentNode.parent != null){
            currentNode = currentNode.parent;
            zoomNode = currentNode;
        }else if (currentNode != null) {
            System.out.println("parent node not exist ");
        }else{
            System.out.println("current node is null");
        }
        zoomNode.printWithNoteCompleteNoChildren();

    }

    private static void viewNote() {
        System.out.println("view note:");
        if(currentNode != null)
            currentNode.printWithNoteCompleteNoChildren();

    }

    private static void editNote() {
        System.out.println("edit note:");
        String notes = "";
        scanner.nextLine(); //skip enter key old from menu
        notes = scanner.nextLine();
        currentNode.notes = notes;
    }

    private static void toggleComplete() {
        System.out.println("toggle complete");
        if(currentNode == null){
            System.out.println("current node is null");
            return;
        }
        currentNode.competed = ! currentNode.competed;
    }

    private static void editCurrentNode() {
        System.out.print("edit current node: enter new text");
        scanner.nextLine();//skip earlier enter key for 5.search -> 5<enter>
        String text = scanner.nextLine();
        if(currentNode != null){
            currentNode.setTitle(text);
        }else{
            System.out.println("current node is null");
        }
    }

    private static void search() {
        /*
        * ask for input
        * then search for partial match with any node in tree
        * if found display or add to list of result
        * next/2: next will print search result
        * prev/1: previous search result
        * quit /0: quit search
        * */
        System.out.println("search-> enter text to search: ");
        scanner.nextLine();// skip enter key entered for this search command
        String input = scanner.nextLine();
        List<Node> searchList = root.search(input);
        if(searchList == null || searchList.isEmpty()){
            System.out.println("zero result for search");
            return;
        }
        int ci = 0;
        Node searchNode = null;
        while(true){
            System.out.println("0.quit search 1.previous 2.next 3.make current search node as curren tnode");
            int num ;
            num = scanner.nextInt();
            if(num ==0){ // quit search
                break;
            }
            switch(num){
                case 1: //previous search
                    System.out.println("previous search");
                    if(ci ==0 ){
                        System.out.println("no previous search");
                    }else {
                        ci = ci -1;
                        searchNode = searchList.get(ci);
                        if(searchNode!= null){
                            searchNode.printNoChildren();
                        }
                    }
                    break;
                case 2: //next search
                    System.out.println("next search");
                    if(ci ==searchList.size()-1 ){
                        System.out.println("no next search");
                    }else {
                        ci = ci + 1;
                        searchNode = searchList.get(ci);
                        if(searchNode!= null){
                            searchNode.printNoChildren();
                        }
                    }
                    break;
                case 3: // set current node
                    break;
                default:
                    System.out.println("Invalid search option");
                    break;
            }
        }
    }

    private static void zoomNodePrint() {
        System.out.println("Zoom Node print");
        if(zoomNode == null){
            System.out.println("Zoom node is empty");
            return;
        }
        zoomNode.print();
    }

    private static void zoomIn() {
        /*
        * set current node as zoom node
        *
        * */
        zoomNode = currentNode ;
    }

    /*
    * delete current node, return new current node if possible
    * */

    public static Node deleteCurrentNode(){
        /*
        * can not delete root node
        * if zoom node deleted then ?
        * if current delete and current node is not root then
        *   set current.parent node as current node
        * */
        if(currentNode == root){
            System.out.println("can not delete root node");
            return null;
        }
        // approach if next sibling exist , make the sibling as current node
            //previous sibling
            //next sibling
        Node currentParent = currentNode.parent;
        if(currentParent == null){
            System.out.println("current node does not have parent, can not delete");
            return null;
        }
        int ci = currentParent.childrenList.indexOf(currentNode);
        int newCi = -1;//parent node
        if(ci != -1){
            if(ci == currentParent.childrenList.size()-1){// last child
                if(ci> 0){ // not first child
                    //set previus sibling of current node as current node , delete the current node
                    newCi = ci-1;
                }
            }else {// not last child
                newCi = ci+1;
            }

            if(newCi == -1 ){ // only one child of current.parent
                currentParent.deleteChild(currentNode);
                currentNode = currentParent;
            } else{
                Node newCurrentNode = currentParent.childrenList.get(newCi);
                currentParent.deleteChild(currentNode);
                currentNode = newCurrentNode;
            }

            return currentNode;
        }

        return null;

    }

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
//                System.out.println("psc:" + prevsc + ", sc:" + sc + ", title:" + title);
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
}
