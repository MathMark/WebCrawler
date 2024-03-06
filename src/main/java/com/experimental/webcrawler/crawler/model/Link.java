package com.experimental.webcrawler.crawler.model;

import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Data
public class Link {
    private String url;
    private String hrefText;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return new EqualsBuilder().append(url, link.url).append(hrefText, link.hrefText).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(url).append(hrefText).toHashCode();
    }
}
