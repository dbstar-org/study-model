package io.github.dbstarll.study.entity.ext;

import io.github.dbstarll.dubai.model.entity.Base;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;

/**
 * 练习的熟练度.
 */
public final class MasterPercent implements Base {
    private static final long serialVersionUID = -5871261000987014190L;

    private float percent;
    private int total;
    private int correct;
    private int bingo;
    private Date last;
    private Date next;

    /**
     * 获得熟练度.
     *
     * @return 熟练度
     */
    public float getPercent() {
        return percent;
    }

    /**
     * 设置熟练度.
     *
     * @param percent 熟练度
     */
    public void setPercent(final float percent) {
        this.percent = percent;
    }

    /**
     * 获得总的练习次数.
     *
     * @return 总的练习次数
     */
    public int getTotal() {
        return total;
    }

    /**
     * 设置总的练习次数.
     *
     * @param total 总的练习次数
     */
    public void setTotal(final int total) {
        this.total = total;
    }

    /**
     * 获得总的正确次数.
     *
     * @return 总的正确次数
     */
    public int getCorrect() {
        return correct;
    }

    /**
     * 设置总的正确次数.
     *
     * @param correct 总的正确次数
     */
    public void setCorrect(final int correct) {
        this.correct = correct;
    }

    /**
     * 获得连续正确次数.
     *
     * @return 连续正确次数
     */
    public int getBingo() {
        return bingo;
    }

    /**
     * 设置连续正确次数.
     *
     * @param bingo 连续正确次数
     */
    public void setBingo(final int bingo) {
        this.bingo = bingo;
    }

    /**
     * 获得最后练习时间.
     *
     * @return 最后练习时间
     */
    public Date getLast() {
        return last;
    }

    /**
     * 设置最后练习时间.
     *
     * @param last 最后练习时间
     */
    public void setLast(final Date last) {
        this.last = last;
    }

    /**
     * 获得下次练习时间.
     *
     * @return 下次练习时间
     */
    public Date getNext() {
        return next;
    }

    /**
     * 设置下次练习时间.
     *
     * @param next 下次练习时间
     */
    public void setNext(final Date next) {
        this.next = next;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof MasterPercent)) {
            return false;
        }

        final MasterPercent that = (MasterPercent) o;
        return new EqualsBuilder()
                .append(getPercent(), that.getPercent())
                .append(getTotal(), that.getTotal())
                .append(getCorrect(), that.getCorrect())
                .append(getBingo(), that.getBingo())
                .append(getLast(), that.getLast())
                .append(getNext(), that.getNext())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getPercent())
                .append(getTotal())
                .append(getCorrect())
                .append(getBingo())
                .append(getLast())
                .append(getNext())
                .toHashCode();
    }

    @Override
    public String toString() {
        return "MasterPercent [percent=" + percent + ", correct=" + bingo + "/" + correct + "/" + total
                + ", last=" + last + ", next=" + next + "]";
    }
}
