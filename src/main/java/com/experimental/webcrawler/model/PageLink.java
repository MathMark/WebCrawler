package com.experimental.webcrawler.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageLink {
    private String href;
    private String text;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageLink pageLink = (PageLink) o;
        return getHref().equals(pageLink.getHref());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHref());
    }
}
