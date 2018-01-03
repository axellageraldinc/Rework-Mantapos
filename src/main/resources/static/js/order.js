var priceTotal = 0;
var counterOrder = 0;
var kembalian = 0;
var tableCart = document.getElementById("tableCart");
var totalBayar = document.getElementById("totalBayar");
var CustNameModal = document.getElementById("custName"); //customer name
var inputCustNameChange = document.getElementById("inputCustNameChange");
var TableNoModal = document.getElementById("table_no");
var inputTableNoChange = document.getElementById("inputTableNoChange"); //table no
var CashModal = document.getElementById("cash"); //calculate kembalian
var txtKembalian = document.getElementById("kembalian");
var inputArrayIdOrder = document.getElementById("array_id_orderChange");
var inputPriceTotal = document.getElementById("inputPriceTotal");
var inputPriceTotalChange = document.getElementById("inputPriceTotalChange");
var array_qtyChange = document.getElementById("array_qtyChange");
var restaurantName = document.getElementById("restaurantNameReceipt");
var restaurantAddress = document.getElementById("restaurantAddressReceipt");
var custNameReceipt = document.getElementById("custNameReceipt");
var tableReceipt = document.getElementById("tableReceipt");
var priceTotalReceipt = document.getElementById("priceTotalReceipt");
var CashReceipt = document.getElementById("CashReceipt");
var ChangeReceipt = document.getElementById("ChangeReceipt");

function startTime() {
    $('#changeModal').modal({ show: false});
    var today = new Date();
    var date = today.getDate();
    var month = today.getMonth();
    var year = today.getFullYear();
    var h = today.getHours();
    var m = today.getMinutes();
    var s = today.getSeconds();
    m = checkTime(m);
    s = checkTime(s);
    document.getElementById('txtJam').innerHTML =
        h + ":" + m + ":" + s;
    document.getElementById("txtDate").innerHTML = date + "-" + month + "-" + year;
    var t = setTimeout(startTime, 500);
}

function checkTime(i) {
    if (i < 10) {i = "0" + i};  // add zero in front of numbers < 10
    return i;
}

function addToCart(id, name, price){
    var idbtn = 'btn' +id;
//   document.getElementById(idPanel).disabled= true;
    var row = tableCart.insertRow(-1);
    var cellName = row.insertCell(0);
    var cellQty = row.insertCell(1);
    var cellPrice = row.insertCell(2);
    var cellPriceTotal = row.insertCell(3);
    var cellRemove = row.insertCell(4);
    var cellIdHidden = row.insertCell(5);

    cellIdHidden.style.display = "none";
    cellIdHidden.innerHTML = id;

    cellName.innerHTML = name;
    cellQty.innerHTML = '<input type="number" value="1" min="1" onchange="updatePrice(this)"/>';
    cellPrice.innerHTML = price;
    cellPriceTotal.innerHTML = price;

    cellRemove.innerHTML = '<a href="#" onclick="deleteItem(this, ' + id + ')" style="text-decoration: none; color: red;">REMOVE</a>';

    //Jika ada order baru, counter++
    counterOrder++;
    var array_id_cart = [];
        for (var i=0; i<counterOrder; i++){
        if(id!=array_id_cart[i]){
              try{
                array_id_cart.push(id);
                var btn = document.getElementById(idbtn);
                btn.disabled = true;
                btn.style.backgroundColor = "#000000"; //change button bgColor
                btn.innerHTML = "Already added"; //change button text

                }
            catch(e){
                alert(e);
            }
        }
        else{
        }
        }

    //set variabel priceTotal menjadi 0 lagi.
    priceTotal = 0;


    //Iterasi sebanyak jumlah counterOrder
    for(i=0; i<counterOrder; i++){
        //nilai priceTotal new adalah priceTotal old ditambahkan dengan hasil kali antara nilai price dengan quantity per rows
        priceTotal+=tableCart.rows[i+1].cells[2].innerHTML * tableCart.rows[i+1].cells[1].children[0].value;
    }

    //set nilai totalBayar
    totalBayar.innerHTML = "Rp " + priceTotal;
    inputPriceTotal.value = priceTotal;


}

function updatePrice(x){
    //variabel baris adalah baris ke-x dari baris tabel yang kita ubah-ubah
    var baris = x.parentNode.parentNode;

    //variabel utk menyimpan nilai dari input quantity
    var col_qty = tableCart.rows[baris.rowIndex].cells[1].children[0].value;
    //variabel utk menyimpan nilai dari item price
    var col_price = tableCart.rows[baris.rowIndex].cells[2].innerHTML;
    //variabel utk menyimpan nilai dari price total
    var col_price_total = tableCart.rows[baris.rowIndex].cells[3].innerHTML;

    //Ketika input quantity berubah,
    //nilai variabel priceTotal dikurangi dengan nilai dari priceTotal yang ada di cell yg bersesuaian
    priceTotal-=col_price_total;
    //nilai variabel priceTotal yang baru adalah nilai priceTotal yang old + nilai cell item price * quantity
    priceTotal += col_price*col_qty;

    //Mengganti nilai dari cell ke-3 tabel (yaitu cell Total Price) dengan nilai cell item price * quantity
    tableCart.rows[baris.rowIndex].cells[3].innerHTML = col_price*col_qty;

    //Update text totalBayar
    totalBayar.innerHTML = "Rp " + priceTotal;
    inputPriceTotal.value = priceTotal;
}

function deleteItem(x, idnya) {
    var idbtn2 = 'btn' + idnya;
    var btn2 = document.getElementById(idbtn2);
        btn2.disabled = false;
        btn2.style.backgroundColor = "#ce0a48"; //change button bgColor
        btn2.innerHTML = "Add to Cart";

    //ambil baris dari item yang sesuai
    var baris = x.parentNode.parentNode;
    //Mengurangi priceTotal dengan total price dari baris yang sesuai
    priceTotal-=tableCart.rows[baris.rowIndex].cells[3].innerHTML;
    //Mengurangi counterOrder, supaya saat ada order baru, counterOrder jumlahnya berkurang 1 setelah di delete ini
    counterOrder--;
    baris.parentNode.removeChild(baris);
    totalBayar.innerHTML = "Rp " + priceTotal;



}

function addOrder() {
    var CashModal = document.getElementById("cash");
    kembalian = CashModal.value - priceTotal;
    var CustNameModal = document.getElementById("custName");
    var inputCustName = document.getElementById("inputCustName");
    inputCustName.value = CustNameModal.value;
    var TableNoModal = document.getElementById("table_no");
    var inputTableNo = document.getElementById("inputTableNo");
    inputTableNo.value = TableNoModal.value;
    var txtKembalian = document.getElementById("kembalian");
    txtKembalian.innerHTML = "Rp " + kembalian;
    var inputArrayIdOrder = document.getElementById("array_id_order");

    var array_id_order = [];
    for (i=0; i<counterOrder; i++){
        array_id_order.push(tableCart.rows[i+1].cells[5].innerHTML);
    }
    inputArrayIdOrder.value = array_id_order;
}

function setNotes() {
    var notesModal = document.getElementById("notes");
    var inputNotes = document.getElementById("inputNotesChange");
    inputNotes.value = notesModal.value;
}

function passToChangeModal() {
    //Passing inputan dari input di modal paymentModal ke input di changeModal (untuk nantinya di
    //insert ke database
    inputCustNameChange.value = CustNameModal.value;
    inputTableNoChange.value = TableNoModal.value;
    if(inputCustNameChange.value==""){
        alert("Isi nama customer!");
    } else if(CashModal.value==""){
        alert("Isi jumlah pembayaran!")
    } else if(CashModal.value<priceTotal){
        alert("Jumlah pembayaran terlalu kecil!");
    } else if(inputTableNoChange.value==""){
        alert("Isi nomor meja!");
    } else if(inputCustNameChange.value!="" && CashModal.value!="" && inputTableNoChange.value!=""){
        $('#changeModal').modal('show');
    }
    kembalian = CashModal.value - priceTotal;
    txtKembalian.innerHTML = "Rp " + kembalian; //set text kembalian

    inputPriceTotalChange.value = inputPriceTotal.value;

    var array_id_order = [];
    for (i=0; i<counterOrder; i++){
        array_id_order.push(tableCart.rows[i+1].cells[5].innerHTML);
    }
    inputArrayIdOrder.value = array_id_order;

    var array_qty = [];
    for(i=0;i<counterOrder; i++){
        array_qty.push(tableCart.rows[i+1].cells[1].children[0].value);
    }

    array_qtyChange.value = array_qty;
}

function setReceiptData() {
    custNameReceipt.innerHTML = inputCustNameChange.value;
    priceTotalReceipt.innerHTML = "Total Price : Rp " + inputPriceTotalChange.value;
    CashReceipt.innerHTML = "Cash : Rp " + CashModal.value;
    ChangeReceipt.innerHTML = "Change : " + txtKembalian.innerHTML;

    for(i=0; i<counterOrder; i++){
        var row = tableReceipt.insertRow(-1);
        var cellMenu = row.insertCell(0);
        var cellQty = row.insertCell(1);
        var cellTotalPrice = row.insertCell(2);
        cellMenu.innerHTML = tableCart.rows[i+1].cells[0].innerHTML;
        cellQty.innerHTML = tableCart.rows[i+1].cells[1].children[0].value;
        cellTotalPrice.innerHTML = tableCart.rows[i+1].cells[3].innerHTML;
    }
}

function chooseLedgerRange(selector){
    var monthSelector = document.getElementById("monthselector");
    var yearselector = document.getElementById("yearselector");
    var date_picker_awal = document.getElementById("date_picker_awal");
    var date_picker_akhir = document.getElementById("date_picker_akhir");
    if(selector==0){
        monthSelector.style.display = "inline";
        yearselector.style.display = "inline";
        date_picker_awal.style.display = "none";
        date_picker_akhir.style.display = "none";
    }
    else if(selector==1){
        monthSelector.style.display = "none";
        yearselector.style.display = "inline";
        date_picker_awal.style.display = "none";
        date_picker_akhir.style.display = "none";
    } else if(selector==2) {
        monthSelector.style.display = "none";
        yearselector.style.display = "none";
        date_picker_awal.style.display = "none";
        date_picker_akhir.style.display = "none";
    } else {
        monthSelector.style.display = "none";
        yearselector.style.display = "none";
        date_picker_awal.style.display = "inline";
        date_picker_akhir.style.display = "inline";
    }
}

function checkboxKirimEmailReceipt(){
    var isChecked = document.getElementById("checkboxKirimEmailReceipt").checked;
    var inputEmailKirimReceipt = document.getElementById("inputEmailKirimReceipt");
    // var emailKirimReceipt = document.getElementById("emailKirimReceipt");
    var is_kirim_email_receipt = document.getElementById("is_kirim_email_receipt");
    if(isChecked == true){
        inputEmailKirimReceipt.style.display = "inline";
        is_kirim_email_receipt.value = "yes"
        // emailKirimReceipt.value = inputEmailKirimReceipt.value;
    } else {
        inputEmailKirimReceipt.style.display = "none";
        is_kirim_email_receipt.value = "no"
    }
}