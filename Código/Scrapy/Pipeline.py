import subprocess

for i in range (1, 5183):
    arg = "page="+str(i)
    subprocess.call(["scrapy", "crawl", "stf", "-a", arg])
