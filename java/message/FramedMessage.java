import lombok.Builder;
import lombok.Singular;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Builder(toBuilder = true, builderClassName = "MessageBuilder", builderMethodName = "create")
public class FramedMessage {

    @Builder.Default
    private int maxRowLength = -1;
    @Builder.Default
    private int columnAmount = 3;
    @Builder.Default
    private Character spacingChar = ' ', wallChar = '║', topChar = '═', bottomChar = '═';
    @Builder.Default
    private Character topLeftCorner = '╔', topRightCorner = '╗', bottomLeftCorner = '╚', bottomRightCorner = '╝';

    @Singular("addLine")
    private List<String> lines;

    public String generate() {
        StringBuilder sb = new StringBuilder();
        int length = Collections.max(lines, Comparator.comparing(String::length)).length();
        String top = topLeftCorner + StringUtils.repeat(topChar.toString(), length + 2) + topRightCorner;
        sb.append(top).append("\n");
        for (int i = 0; i < columnAmount; i++) {
            sb.append(wallChar).append(" ");
            if (maxRowLength == -1)
                sb.append(StringUtils.center(lines.get(i), length, spacingChar));
            else if (lines.get(i).length() + length < maxRowLength)
                sb.append(StringUtils.repeat(" ", length));
            sb.append(" ").append(wallChar).append("\n");
        }
        String bottom = bottomLeftCorner + StringUtils.repeat(bottomChar.toString(), length + 2) + bottomRightCorner;
        sb.append(bottom).append("\n");
        return sb.toString();
    }

}
