import pandas as pd

in_path  = "약품명목록.csv"
out_path = "약품명목록(중복제거).csv"
encoding_in = "cp949"       # 필요시 "utf-8-sig"
encoding_out = "utf-8-sig"  # 저장 인코딩

df = pd.read_csv(in_path, encoding=encoding_in)
df = df.drop_duplicates(keep="first")  # 동일 행은 첫 번째만 남김
df.to_csv(out_path, index=False, encoding=encoding_out)
