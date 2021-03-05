package morn.xposed.alipay.ui;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Comparator;

public class ForestUser extends AlipayId implements Comparable<ForestUser> {
    public long addTime;
    public int collectEnergySum;
    public int collectEnergyWeek;
    public int energySum;
    public int energyWeek;
    public int friendStatus;
    public int week;

    public static int getAvgCollect(ForestUser user) {
        int days = calculateDays(user.addTime, user.week);
        double totalEnergy = user.collectEnergySum + user.collectEnergyWeek;
        totalEnergy /= days;
        if (user.energySum < 0) {
            return 0;
        }
        return (int) totalEnergy;
    }

    public static Comparator<ForestUser> getAvgCollectComparator() {
        return new Comparator<ForestUser>() {
            @Override
            public int compare(ForestUser o1, ForestUser o2) {
                int t = o1.friendStatus - o2.friendStatus;
                if (t == 0) {
                    t = getAvgCollect(o1) - getAvgCollect(o2);
                    if (t == 0) {
                        t = o1.collectEnergyWeek - o2.collectEnergyWeek;
                        if (t == 0) {
                            t = o1.collectEnergySum - o2.collectEnergySum;
                            if (t == 0) {
                                t = o1.energyWeek - o2.energyWeek;
                                if (t == 0) {
                                    t = (int) (o1.addTime - o2.addTime);
                                    if (t == 0) {
                                        return o1.energySum - o2.energySum;
                                    }
                                    return t;
                                }
                                return t;
                            }
                            return t;
                        }
                        return t;
                    }
                    return t;
                }
                return t;
            }
        };
    }

    public static Comparator<ForestUser> getSumCollectComparator() {
        return new Comparator<ForestUser>() {
            @Override
            public int compare(ForestUser o1, ForestUser o2) {
                int t = o1.friendStatus - o2.friendStatus;
                if (t == 0) {
                    t = o1.collectEnergySum - o2.collectEnergySum;
                    if (t == 0) {
                        t = getAvgCollect(o1) - getAvgCollect(o2);
                        if (t == 0) {
                            t = o1.collectEnergyWeek - o2.collectEnergyWeek;
                            if (t == 0) {
                                t = o1.energyWeek - o2.energyWeek;
                                if (t == 0) {
                                    t = (int) (o1.addTime - o2.addTime);
                                    if (t == 0) {
                                        return o1.energySum - o2.energySum;
                                    }
                                    return t;
                                }
                                return t;
                            }
                            return t;
                        }
                        return t;
                    }
                    return t;
                }
                return t;
            }
        };
    }

    public static Comparator<ForestUser> getSumComparator() {
        return new Comparator<ForestUser>() {
            @Override
            public int compare(ForestUser o1, ForestUser o2) {
                int t = o1.friendStatus - o2.friendStatus;
                if (t == 0) {
                    t = o1.energySum - o2.energySum;
                    if (t == 0) {
                        t = getAvgCollect(o1) - getAvgCollect(o2);
                        if (t == 0) {
                            t = o1.collectEnergyWeek - o2.collectEnergyWeek;
                            if (t == 0) {
                                t = o1.collectEnergySum - o2.collectEnergySum;
                                if (t == 0) {
                                    t = o1.energyWeek - o2.energyWeek;
                                    if (t == 0) {
                                        return (int) (o1.addTime - o2.addTime);
                                    }
                                    return t;
                                }
                                return t;
                            }
                            return t;
                        }
                        return t;
                    }
                    return t;
                }
                return t;
            }
        };
    }

    @Deprecated
    public static Comparator<ForestUser> getSumComparatorBak() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }
        int finalDayOfWeek = dayOfWeek;
        return new Comparator<ForestUser>() {
            @Override
            public int compare(ForestUser o1, ForestUser o2) {
                int t = o1.friendStatus - o2.friendStatus;
                if (t == 0) {
                    t = o1.energySum - o2.energySum;
                    if (t == 0) {
                        t = calculateDays(o1, finalDayOfWeek) - calculateDays(o2, finalDayOfWeek);
                        if (t == 0) {
                            t = o1.collectEnergyWeek - o2.collectEnergyWeek;
                            if (t == 0) {
                                t = o1.collectEnergySum - o2.collectEnergySum;
                                if (t == 0) {
                                    return o1.energyWeek - o2.energyWeek;
                                }
                                return t;
                            }
                            return t;
                        }
                        return t;
                    }
                    return t;
                }
                return t;
            }
        };
    }

    public static Comparator<ForestUser> getWeekCollectComparator() {
        return new Comparator<ForestUser>() {
            @Override
            public int compare(ForestUser o1, ForestUser o2) {
                int t = o1.friendStatus - o2.friendStatus;
                if (t == 0) {
                    t = o1.collectEnergyWeek - o2.collectEnergyWeek;
                    if (t == 0) {
                        t = getAvgCollect(o1) - getAvgCollect(o2);
                        if (t == 0) {
                            t = o1.collectEnergySum - o2.collectEnergySum;
                            if (t == 0) {
                                t = o1.energyWeek - o2.energyWeek;
                                if (t == 0) {
                                    t = (int) (o1.addTime - o2.addTime);
                                    if (t == 0) {
                                        return o1.energySum - o2.energySum;
                                    }
                                    return t;
                                }
                                return t;
                            }
                            return t;
                        }
                        return t;
                    }
                    return t;
                }
                return t;
            }
        };
    }

    public static Comparator<ForestUser> getWeekComparator() {
        return new Comparator<ForestUser>() {
            @Override
            public int compare(ForestUser o1, ForestUser o2) {
                int t = o1.friendStatus - o2.friendStatus;
                if (t == 0) {
                    t = o1.energyWeek - o2.energyWeek;
                    if (t == 0) {
                        t = getAvgCollect(o1) - getAvgCollect(o2);
                        if (t == 0) {
                            t = o1.collectEnergyWeek - o2.collectEnergyWeek;
                            if (t == 0) {
                                t = o1.collectEnergySum - o2.collectEnergySum;
                                if (t == 0) {
                                    t = (int) (o1.addTime - o2.addTime);
                                    if (t == 0) {
                                        return o1.energySum - o2.energySum;
                                    }
                                    return t;
                                }
                                return t;
                            }
                            return t;
                        }
                        return t;
                    }
                    return t;
                }
                return t;
            }
        };
    }

    public static ForestUser parseJSONObject(JSONObject jsonObject) {
        ForestUser user = new ForestUser();
        user.id = jsonObject.optString("id");
        user.name = jsonObject.optString("name");
        user.energyWeek = jsonObject.optInt("energyWeek");
        user.energySum = jsonObject.optInt("energySum");
        user.collectEnergyWeek = jsonObject.optInt("collectEnergyWeek");
        user.collectEnergySum = jsonObject.optInt("collectEnergySum");
        user.week = jsonObject.optInt("week");
        user.friendStatus = jsonObject.optInt("friendStatus");
        user.addTime = jsonObject.optLong("addTime");
        return user;
    }

    private static int calculateDays(long addTime, int week) {
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.HOUR_OF_DAY, 0);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND, 0);
        c1.set(Calendar.MILLISECOND, 0);
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(addTime);
        c2.set(Calendar.HOUR_OF_DAY, 0);
        c2.set(Calendar.MINUTE, 0);
        c2.set(Calendar.SECOND, 0);
        c2.set(Calendar.MILLISECOND, 0);
        int days = (int) ((c1.getTimeInMillis() - c2.getTimeInMillis()) / 86400000L);
        int dayOfWeek = c1.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }
        return Math.min(week * 7 + dayOfWeek, days + 1);
    }

    private static int calculateDays(ForestUser user, int days) {
        return (user.collectEnergySum + user.collectEnergyWeek) / (user.week * 7 + days);
    }

    @Override
    public int compareTo(ForestUser other) {
        int t = this.friendStatus - other.friendStatus;
        if (t == 0) {
            t = this.collectEnergyWeek - other.collectEnergyWeek;
            if (t == 0) {
                t = this.collectEnergySum - other.collectEnergySum;
                if (t == 0) {
                    t = this.energyWeek - other.energyWeek;
                    if (t == 0) {
                        return this.energySum - other.energySum;
                    }
                    return t;
                }
                return t;
            }
            return t;
        }
        return t;
    }
}