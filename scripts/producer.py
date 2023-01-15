# coding: utf-8
import csv
import time

if __name__ == '__main__':   #筛选出2017-11-25至2017-12-03时间内前100W条收藏、加购、购买的用户行为
    start_time = time.mktime(time.strptime('2017-11-25', '%Y-%m-%d'))
    end_time = time.mktime(time.strptime('2017-12-03', '%Y-%m-%d'))
    i = 0
    with open('../data/UserBehavior.csv', 'r') as fr:
        reader = csv.reader(fr)
        for row in reader:
            #if row[3] != 'pv':
            if start_time < int(row[4]) < end_time :
                str_time = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime(int(row[4])))
                info = [row[0], row[1], row[3], str_time]
                i +=1
                if i > 1000000:
                    break
                print(i, info)
                with open("../data/Processed_UserBehavior.csv", 'a+', newline='') as fw:
                    writer = csv.writer(fw)
                    writer.writerow(info)
