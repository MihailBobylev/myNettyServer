package models;

public class Graph {
    /*//массив для хранения вершин
    private Area[] vertexArray;
    //матрица смежности
    private int[][] matrix;
    //текущее количество вершин
    private int count;

    Stack<Integer> stack = new Stack<Integer>();

    public Graph(int n)
    {
        vertexArray = new Area[n];
        matrix = new int[n][n];
        for(int i = 0; i < n; i++)
            for(int j = 0; j < n; j++)
                matrix[i][j] = 0;
    }

    public void insertArea(Integer id, String housing, Integer floor, String name)
    {
        Area v = new Area(id, housing, floor, name);
        vertexArray[count++] = v;
    }

    public void insertEdge(int begin, int end)
    {
        matrix[begin][end] = 1;
        matrix[end][begin] = 1;
    }

    //получение смежной непосещенной вершины
    private int getUnvisitedVertex(int vertex)
    {
        for(int i = 0; i < count; i++)
            if(matrix[vertex][i] == 1 && vertexArray[i].getVisited() == false)
                return i;

        return -1;
    }

    //реализация обхода в глубину
    public void dfs(int v)
    {
        System.out.print("Выполняем обход в глубину: " + vertexArray[v].getName() + " -> ");
        vertexArray[v].setVisited(true);
        stack.push(v);
        while(!stack.isEmpty())
        {
            int adjVertex = getUnvisitedVertex(stack.peek());
            if(adjVertex == -1)
                stack.pop();
            else{
                vertexArray[adjVertex].setVisited(true);
                System.out.print(vertexArray[adjVertex].getName() + " -> ");
                stack.push(adjVertex);
            }
        }

        for(Area vertex: vertexArray)
            vertex.setVisited(false);
    }*/
}
