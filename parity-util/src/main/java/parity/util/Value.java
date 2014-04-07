package parity.util;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Value {

    private static final ToStringStyle STYLE = new StandardToStringStyle() {
	{
	    setUseShortClassName(true);
	    setUseFieldNames(false);
	    setUseIdentityHashCode(false);
	    setContentStart("(");
	    setContentEnd(")");
	}
    };

    @Override
    public boolean equals(Object obj) {
	return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
	return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
	return ToStringBuilder.reflectionToString(this, STYLE);
    }

}
