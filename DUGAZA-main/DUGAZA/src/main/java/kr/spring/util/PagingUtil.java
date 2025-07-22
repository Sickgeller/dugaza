package kr.spring.util;

public class PagingUtil {
	private int startRow;	 
	private int endRow;	 
	private StringBuffer page;

    private int currentPage;
    private int count;
    private int rowCount;
    private int totalPage;
    private int startPage;
    private int endPage;
    private int pageCount;

	/**
	 * keyfield : 검색 필드
	 * keyword : 검색어
	 * currentPage : 현재페이지
	 * count : 전체 게시물 수
	 * rowCount : 한 페이지의  게시물의 수 
	 * pageCount : 한 화면에 보여줄 페이지 수
	 * pageUrl : 호출 페이지 url
	 * addKey : 부가적인 key ("&num=23"형식으로 전달할 것)
	 * */
	public PagingUtil(int currentPage,int count, int rowCount) {
		this(null,null,currentPage,count,rowCount,0,null,null);
	}
	public PagingUtil(int currentPage, int count, int rowCount,
			int pageCount, String pageUrl) {
		this(null,null,currentPage,count,rowCount,pageCount,pageUrl,null);
	}
	public PagingUtil(int currentPage, int count, int rowCount,
			int pageCount, String pageUrl, String addKey) {
		this(null,null,currentPage,count,rowCount,pageCount,pageUrl,addKey);
	}
	public PagingUtil(String keyfield, String keyword, int currentPage, int count, int rowCount,
			int pageCount,String pageUrl) {
		this(keyfield,keyword,currentPage,count,rowCount,pageCount,pageUrl,null);
	}
	public PagingUtil(String keyfield, String keyword, int currentPage, int count, int rowCount,
			int pageCount,String pageUrl,String addKey) {

        this.count = count;
        this.rowCount = rowCount;
        this.currentPage = currentPage;
        this.pageCount = pageCount;

		if(count >= 0) {
			String sub_url = "";
			if(keyword != null && !"".equals(keyword)) sub_url = "&keyfield="+keyfield+"&keyword="+keyword;
			if(addKey != null) sub_url += addKey;

			totalPage = (int) Math.ceil((double) count / rowCount);
			if (totalPage == 0) {
				totalPage = 1;
			}
			if (this.currentPage > totalPage) {
				this.currentPage = totalPage;
			}
			startRow = (this.currentPage - 1) * rowCount + 1;
			endRow = this.currentPage * rowCount;
			
			page = new StringBuffer();
			if(pageCount > 0) {
				startPage = (int) ((this.currentPage - 1) / pageCount) * pageCount + 1;
				endPage = startPage + pageCount - 1;
				if (endPage > totalPage) {
					endPage = totalPage;
				}
				
				if (this.currentPage > pageCount) {
					page.append("<a href="+pageUrl+"?pageNum="+ (startPage - 1) + sub_url +">");
					page.append("[이전]");
					page.append("</a>");
				}
				for (int i = startPage; i <= endPage; i++) {
					if (i > totalPage) {
						break;
					}
					if (i == this.currentPage) {
						page.append("&nbsp;<b><span style='color:red;'>");
						page.append(i);
						page.append("</span></b>");
					} else {
						page.append("&nbsp;<a href='"+pageUrl+"?pageNum=");
						page.append(i);
						page.append(sub_url+"'>");
						page.append(i);
						page.append("</a>");
					}
					page.append("&nbsp;");
				}
				if (totalPage - startPage >= pageCount) {
					page.append("<a href="+pageUrl+"?pageNum="+ (endPage + 1) + sub_url +">");
					page.append("[다음]");
					page.append("</a>");
				}
			}else {
				page.append("<b>[warning]</b>pageCount는 1이상 지정해야 페이지수가 표시됩니다.");
			}
		}
	}

	public StringBuffer getPage() {
		return page;
	}
	public int getStartRow() {
		return startRow;
	}
	public int getEndRow() {
		return endRow;
	}
    public int getCurrentPage() { return currentPage; }
    public int getCount() { return count; }
    public int getTotalPage() { return totalPage; }
    public int getStartPage() { return startPage; }
    public int getEndPage() { return endPage; }
    public int getPageCount() { return pageCount; }
}
