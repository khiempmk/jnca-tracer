package tracer.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Node {
    int id ;
    double xCoord ;
    double yCoord ;

    public Node(int id, double xCoord, double yCoord) {
        this.id = id;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }
}
